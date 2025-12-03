package com.mbongo.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Expense
import com.mbongo.app.data.local.entity.Category
import com.mbongo.app.data.repository.ExpenseRepository
import com.mbongo.app.data.repository.CategoryRepository
import com.mbongo.app.data.repository.IncomeRepository
import com.mbongo.app.data.repository.RemoteRepository
import com.mbongo.app.data.repository.ApiResult
import com.mbongo.app.data.remote.dto.CreateExpenseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

sealed class ExpenseResult {
    object Success : ExpenseResult()
    data class Error(val message: String) : ExpenseResult()
}

// Modèle unifié pour affichage
data class ExpenseDisplay(
    val id: Long,
    val amount: Double,
    val description: String?,
    val date: String,
    val categoryId: Long,
    val categoryName: String?,
    val color: String?,
    val icon: String?
)

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val incomeRepository: IncomeRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val _currentMonth = MutableStateFlow(dateFormat.format(Date()))
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()
    
    private val _useRemote = MutableStateFlow(true)
    val useRemote: StateFlow<Boolean> = _useRemote.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseDisplay>>(emptyList())
    val expenses: StateFlow<List<ExpenseDisplay>> = _expenses.asStateFlow()

    // Catégories depuis l'API
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()

    private val _hasSalary = MutableStateFlow(true) // Par défaut true pour l'API distante
    val hasSalary: StateFlow<Boolean> = _hasSalary.asStateFlow()

    private val _addExpenseResult = MutableSharedFlow<ExpenseResult>()
    val addExpenseResult: SharedFlow<ExpenseResult> = _addExpenseResult.asSharedFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadExpenses()
        loadCategories()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            if (_useRemote.value) {
                remoteRepository.getCategories().collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            _categories.value = result.data.map { dto ->
                                Category(
                                    id = dto.id,
                                    name = dto.name,
                                    color = dto.color ?: "#10b981",
                                    icon = dto.icon ?: "📦",
                                    type = dto.type ?: "expense"
                                )
                            }
                            Log.d("ExpensesViewModel", "Loaded ${result.data.size} categories from API")
                        }
                        is ApiResult.Error -> {
                            Log.e("ExpensesViewModel", "Error loading categories: ${result.message}")
                            // Fallback vers local
                            categoryRepository.getCategoriesByType("expense").collect { localCats ->
                                _categories.value = localCats
                            }
                        }
                        is ApiResult.Loading -> {}
                    }
                }
            } else {
                categoryRepository.getCategoriesByType("expense").collect { localCats ->
                    _categories.value = localCats
                }
            }
        }
    }
    
    private fun loadExpenses() {
        viewModelScope.launch {
            val month = _currentMonth.value
            val parts = month.split("-")
            val year = parts[0].toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
            val monthNum = parts[1].toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
            
            _isLoading.value = true
            
            if (_useRemote.value) {
                loadExpensesFromRemote(monthNum, year, month)
            } else {
                loadExpensesFromLocal()
            }
        }
    }
    
    private suspend fun loadExpensesFromRemote(monthNum: Int, year: Int, month: String) {
        remoteRepository.getExpenses(monthNum, year).collect { result ->
            when (result) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _expenses.value = result.data.map { dto ->
                        ExpenseDisplay(
                            id = dto.id,
                            amount = dto.amount,
                            description = dto.description,
                            date = dto.date,
                            categoryId = dto.categoryId,
                            categoryName = dto.categoryName,
                            color = dto.color,
                            icon = dto.icon
                        )
                    }
                    _totalExpenses.value = result.data.sumOf { it.amount }
                    _isLoading.value = false
                    
                    // Vérifier si salaire existe via revenus
                    checkSalaryFromRemote(month)
                }
                is ApiResult.Error -> {
                    Log.e("ExpensesViewModel", "Remote error: ${result.message}")
                    _isLoading.value = false
                    loadExpensesFromLocal()
                }
            }
        }
    }
    
    private suspend fun checkSalaryFromRemote(month: String) {
        remoteRepository.getIncomes(month).collect { result ->
            if (result is ApiResult.Success) {
                _hasSalary.value = result.data.any { it.type == "salary" }
            }
        }
    }
    
    private fun loadExpensesFromLocal() {
        viewModelScope.launch {
            expenseRepository.getAllExpenses().collect { localExpenses ->
                _expenses.value = localExpenses.map { expense ->
                    ExpenseDisplay(
                        id = expense.id,
                        amount = expense.amount,
                        description = expense.description,
                        date = expense.date,
                        categoryId = expense.categoryId,
                        categoryName = null,
                        color = null,
                        icon = null
                    )
                }
                _totalExpenses.value = localExpenses.sumOf { it.amount }
                _isLoading.value = false
            }
        }
        checkSalary()
    }

    private fun checkSalary() {
        viewModelScope.launch {
            _hasSalary.value = incomeRepository.hasSalaryForMonth(_currentMonth.value)
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            if (_useRemote.value) {
                val dto = CreateExpenseDto(
                    amount = expense.amount,
                    description = expense.description,
                    date = expense.date,
                    categoryId = expense.categoryId
                )
                val result = remoteRepository.createExpense(dto)
                if (result is ApiResult.Success) {
                    _addExpenseResult.emit(ExpenseResult.Success)
                    loadExpenses()
                } else if (result is ApiResult.Error) {
                    _addExpenseResult.emit(ExpenseResult.Error(result.message))
                }
            } else {
                // Vérifier si un salaire existe pour ce mois
                val hasSalaryForMonth = incomeRepository.hasSalaryForMonth(_currentMonth.value)
                
                if (!hasSalaryForMonth) {
                    _addExpenseResult.emit(
                        ExpenseResult.Error("Vous devez d'abord enregistrer un salaire pour ce mois avant d'ajouter des dépenses.")
                    )
                    return@launch
                }
                
                expenseRepository.insertExpense(expense)
                _addExpenseResult.emit(ExpenseResult.Success)
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseDisplay) {
        viewModelScope.launch {
            if (_useRemote.value) {
                val result = remoteRepository.deleteExpense(expense.id)
                if (result is ApiResult.Success) {
                    loadExpenses()
                }
            } else {
                expenseRepository.deleteExpense(Expense(
                    id = expense.id,
                    amount = expense.amount,
                    description = expense.description,
                    date = expense.date,
                    categoryId = expense.categoryId
                ))
            }
        }
    }
    
    fun refresh() {
        loadExpenses()
    }
}
