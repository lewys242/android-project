package com.mbongo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Income
import com.mbongo.app.data.local.entity.Category
import com.mbongo.app.data.repository.IncomeRepository
import com.mbongo.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val incomes: StateFlow<List<Income>> = incomeRepository.getAllIncomes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesByType("income")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalIncomes: StateFlow<Double> = incomeRepository.getTotalIncomes()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun addIncome(income: Income) {
        viewModelScope.launch {
            incomeRepository.insertIncome(income)
        }
    }

    fun updateIncome(income: Income) {
        viewModelScope.launch {
            incomeRepository.updateIncome(income)
        }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch {
            incomeRepository.deleteIncome(income)
        }
    }
}
