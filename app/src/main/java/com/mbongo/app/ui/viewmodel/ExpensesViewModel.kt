package com.mbongo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Expense
import com.mbongo.app.data.local.entity.Category
import com.mbongo.app.data.repository.ExpenseRepository
import com.mbongo.app.data.repository.CategoryRepository
import com.mbongo.app.data.repository.IncomeRepository
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

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val incomeRepository: IncomeRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val _currentMonth = MutableStateFlow(dateFormat.format(Date()))
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    val expenses: StateFlow<List<Expense>> = expenseRepository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesByType("expense")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalExpenses: StateFlow<Double> = expenseRepository.getTotalExpenses()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    private val _hasSalary = MutableStateFlow(false)
    val hasSalary: StateFlow<Boolean> = _hasSalary.asStateFlow()

    private val _addExpenseResult = MutableSharedFlow<ExpenseResult>()
    val addExpenseResult: SharedFlow<ExpenseResult> = _addExpenseResult.asSharedFlow()

    init {
        checkSalary()
    }

    private fun checkSalary() {
        viewModelScope.launch {
            _hasSalary.value = incomeRepository.hasSalaryForMonth(_currentMonth.value)
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
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

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }
    
    fun refresh() {
        checkSalary()
    }
}
