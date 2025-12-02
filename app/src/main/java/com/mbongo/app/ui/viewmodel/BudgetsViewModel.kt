package com.mbongo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Budget
import com.mbongo.app.data.local.entity.Category
import com.mbongo.app.data.repository.BudgetRepository
import com.mbongo.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val budgets: StateFlow<List<Budget>> = budgetRepository.getAllBudgets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeBudgets: StateFlow<List<Budget>> = budgetRepository.getActiveBudgets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesByType("expense")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalBudgetAmount: StateFlow<Double> = budgetRepository.getTotalBudgetAmount()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.insertBudget(budget)
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.updateBudget(budget)
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(budget)
        }
    }
}
