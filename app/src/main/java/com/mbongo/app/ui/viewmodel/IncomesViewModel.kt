package com.mbongo.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Income
import com.mbongo.app.data.local.entity.Category
import com.mbongo.app.data.repository.IncomeRepository
import com.mbongo.app.data.repository.CategoryRepository
import com.mbongo.app.data.repository.RemoteRepository
import com.mbongo.app.data.repository.ApiResult
import com.mbongo.app.data.remote.dto.CreateIncomeDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// Modèle unifié pour affichage
data class IncomeDisplay(
    val id: Long,
    val amount: Double,
    val description: String?,
    val month: String,
    val date: String?,
    val type: String?
)

@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val categoryRepository: CategoryRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val _currentMonth = MutableStateFlow(dateFormat.format(Date()))
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()
    
    private val _useRemote = MutableStateFlow(true)

    private val _incomes = MutableStateFlow<List<IncomeDisplay>>(emptyList())
    val incomes: StateFlow<List<IncomeDisplay>> = _incomes.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesByType("income")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _totalIncomes = MutableStateFlow(0.0)
    val totalIncomes: StateFlow<Double> = _totalIncomes.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadIncomes()
    }
    
    private fun loadIncomes() {
        viewModelScope.launch {
            val month = _currentMonth.value
            _isLoading.value = true
            
            if (_useRemote.value) {
                loadIncomesFromRemote(month)
            } else {
                loadIncomesFromLocal()
            }
        }
    }
    
    private suspend fun loadIncomesFromRemote(month: String) {
        remoteRepository.getIncomes(month).collect { result ->
            when (result) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _incomes.value = result.data.map { dto ->
                        IncomeDisplay(
                            id = dto.id,
                            amount = dto.amount,
                            description = dto.description,
                            month = dto.month,
                            date = dto.date,
                            type = dto.type
                        )
                    }
                    _totalIncomes.value = result.data.sumOf { it.amount }
                    _isLoading.value = false
                }
                is ApiResult.Error -> {
                    Log.e("IncomesViewModel", "Remote error: ${result.message}")
                    _isLoading.value = false
                    loadIncomesFromLocal()
                }
            }
        }
    }
    
    private fun loadIncomesFromLocal() {
        viewModelScope.launch {
            incomeRepository.getAllIncomes().collect { localIncomes ->
                _incomes.value = localIncomes.map { income ->
                    IncomeDisplay(
                        id = income.id,
                        amount = income.amount,
                        description = income.description,
                        month = income.month,
                        date = income.date,
                        type = income.type
                    )
                }
                _totalIncomes.value = localIncomes.sumOf { it.amount }
                _isLoading.value = false
            }
        }
    }

    fun addIncome(income: Income) {
        viewModelScope.launch {
            if (_useRemote.value) {
                val dto = CreateIncomeDto(
                    amount = income.amount,
                    description = income.description,
                    month = income.month,
                    date = income.date,
                    type = income.type,
                    categoryId = null
                )
                val result = remoteRepository.createIncome(dto)
                if (result is ApiResult.Success) {
                    loadIncomes()
                }
            } else {
                incomeRepository.insertIncome(income)
            }
        }
    }

    fun updateIncome(income: Income) {
        viewModelScope.launch {
            incomeRepository.updateIncome(income)
        }
    }

    fun deleteIncome(income: IncomeDisplay) {
        viewModelScope.launch {
            if (_useRemote.value) {
                val result = remoteRepository.deleteIncome(income.id)
                if (result is ApiResult.Success) {
                    loadIncomes()
                }
            } else {
                incomeRepository.deleteIncome(Income(
                    id = income.id,
                    amount = income.amount,
                    description = income.description,
                    month = income.month,
                    date = income.date ?: "",
                    type = income.type ?: "other"
                ))
            }
        }
    }
    
    fun refresh() {
        loadIncomes()
    }
}
