package com.mbongo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Category
import com.mbongo.app.data.repository.ExpenseRepository
import com.mbongo.app.data.repository.IncomeRepository
import com.mbongo.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class CategoryStats(
    val category: Category,
    val total: Double,
    val percentage: Float
)

data class StatisticsState(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,
    val usedPercentage: Float = 0f,
    val categoryStats: List<CategoryStats> = emptyList(),
    val monthlyTotals: List<Pair<String, Double>> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    
    private val _currentMonth = MutableStateFlow(dateFormat.format(Date()))
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()
    
    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR).toString())
    val currentYear: StateFlow<String> = _currentYear.asStateFlow()
    
    private val _viewMode = MutableStateFlow("month") // "month" ou "year"
    val viewMode: StateFlow<String> = _viewMode.asStateFlow()
    
    private val _statisticsState = MutableStateFlow(StatisticsState())
    val statisticsState: StateFlow<StatisticsState> = _statisticsState.asStateFlow()
    
    private val _categories = MutableStateFlow<Map<Long, Category>>(emptyMap())

    init {
        loadCategories()
        loadStatistics()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategoriesByType("expense").collect { cats ->
                _categories.value = cats.associateBy { it.id }
                // Recharger les stats avec les catégories
                loadStatistics()
            }
        }
    }

    fun setMonth(month: Int, year: Int) {
        _currentMonth.value = String.format("%04d-%02d", year, month)
        _currentYear.value = year.toString()
        loadStatistics()
    }

    fun setViewMode(mode: String) {
        _viewMode.value = mode
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _statisticsState.update { it.copy(isLoading = true) }
            
            try {
                val isMonthView = _viewMode.value == "month"
                
                // Charger les totaux
                val totalIncome = if (isMonthView) {
                    incomeRepository.getTotalIncomeByMonth(_currentMonth.value)
                } else {
                    // Total des revenus sur l'année
                    var yearTotal = 0.0
                    for (m in 1..12) {
                        val month = String.format("%s-%02d", _currentYear.value, m)
                        yearTotal += incomeRepository.getTotalIncomeByMonth(month)
                    }
                    yearTotal
                }
                
                val totalExpenses = if (isMonthView) {
                    expenseRepository.getTotalExpensesByMonth(_currentMonth.value)
                } else {
                    expenseRepository.getTotalExpensesByYear(_currentYear.value)
                }
                
                val balance = totalIncome - totalExpenses
                val usedPercentage = if (totalIncome > 0) {
                    ((totalExpenses / totalIncome) * 100).toFloat().coerceIn(0f, 100f)
                } else 0f
                
                // Statistiques par catégorie
                val categoryTotals = if (isMonthView) {
                    expenseRepository.getExpensesByCategoryForMonth(_currentMonth.value)
                } else {
                    expenseRepository.getExpensesByCategoryForYear(_currentYear.value)
                }
                
                val categoryStats = categoryTotals.mapNotNull { ct ->
                    _categories.value[ct.categoryId]?.let { category ->
                        val pct = if (totalExpenses > 0) {
                            ((ct.total / totalExpenses) * 100).toFloat()
                        } else 0f
                        CategoryStats(category, ct.total, pct)
                    }
                }.sortedByDescending { it.total }
                
                // Totaux mensuels pour le graphique d'évolution
                val monthlyTotals = expenseRepository.getMonthlyTotalsForYear(_currentYear.value)
                val monthNames = listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Jun", 
                                       "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc")
                val monthlyData = (1..12).map { m ->
                    val monthNum = String.format("%02d", m)
                    val total = monthlyTotals.find { it.monthNum == monthNum }?.total ?: 0.0
                    monthNames[m-1] to total
                }
                
                _statisticsState.update {
                    StatisticsState(
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        balance = balance,
                        usedPercentage = usedPercentage,
                        categoryStats = categoryStats,
                        monthlyTotals = monthlyData,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _statisticsState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun refresh() {
        loadStatistics()
    }
}
