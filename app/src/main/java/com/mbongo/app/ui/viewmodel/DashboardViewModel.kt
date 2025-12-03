package com.mbongo.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// Niveau de gestion budgétaire
enum class ManagementLevel {
    GOOD,      // < 80% utilisé
    WARNING,   // 80-100% utilisé
    BAD        // > 100% utilisé
}

data class DashboardUiState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalSavings: Double = 0.0,
    val totalLoans: Double = 0.0,
    val currentMonth: String = "",
    val hasSalary: Boolean = false,
    val usagePercent: Int = 0,
    val managementLevel: ManagementLevel = ManagementLevel.GOOD,
    val previousMonthIncome: Double = 0.0,
    val previousMonthExpenses: Double = 0.0,
    val previousMonthBalance: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val useRemote: Boolean = true // Utiliser l'API distante par défaut
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val loanRepository: LoanRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    
    private val _currentMonth = MutableStateFlow(dateFormat.format(Date()))
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    init {
        loadDashboardData()
    }

    fun setMonth(month: String) {
        _currentMonth.value = month
        loadDashboardData()
    }

    private fun getPreviousMonth(month: String): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        calendar.time = sdf.parse(month) ?: Date()
        calendar.add(Calendar.MONTH, -1)
        return sdf.format(calendar.time)
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val month = _currentMonth.value
            val previousMonth = getPreviousMonth(month)
            
            // Extraire mois et année
            val parts = month.split("-")
            val year = parts[0].toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
            val monthNum = parts[1].toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
            
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Essayer de charger depuis l'API distante d'abord
            if (_uiState.value.useRemote) {
                loadFromRemote(monthNum, year, month, previousMonth)
            } else {
                loadFromLocal(month, previousMonth)
            }
        }
    }
    
    private suspend fun loadFromRemote(monthNum: Int, year: Int, month: String, previousMonth: String) {
        remoteRepository.getStats(monthNum, year).collect { result ->
            when (result) {
                is ApiResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
                is ApiResult.Success -> {
                    val stats = result.data
                    val balance = stats.totalIncome - stats.totalExpenses
                    val usagePercent = if (stats.totalIncome > 0) {
                        ((stats.totalExpenses / stats.totalIncome) * 100).toInt().coerceAtMost(999)
                    } else 0
                    
                    val managementLevel = when {
                        usagePercent > 100 -> ManagementLevel.BAD
                        usagePercent > 80 -> ManagementLevel.WARNING
                        else -> ManagementLevel.GOOD
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        balance = balance,
                        totalIncome = stats.totalIncome,
                        totalExpenses = stats.totalExpenses,
                        totalSavings = balance.coerceAtLeast(0.0),
                        currentMonth = month,
                        hasSalary = stats.totalIncome > 0,
                        usagePercent = usagePercent,
                        managementLevel = managementLevel,
                        isLoading = false,
                        error = null
                    )
                    
                    // Charger les prêts
                    loadLoansFromRemote()
                }
                is ApiResult.Error -> {
                    Log.e("DashboardViewModel", "Remote error: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Connexion au serveur impossible. Vérifiez que le serveur est démarré sur le port 5000."
                    )
                    // Fallback vers données locales
                    loadFromLocal(month, previousMonth)
                }
            }
        }
    }
    
    private suspend fun loadLoansFromRemote() {
        remoteRepository.getLoans().collect { result ->
            if (result is ApiResult.Success) {
                val totalLoans = result.data.sumOf { loan ->
                    val totalInterest = loan.principal * (loan.interestRate / 100)
                    loan.principal + totalInterest - (loan.totalRepaid ?: 0.0)
                }
                _uiState.value = _uiState.value.copy(totalLoans = totalLoans)
            }
        }
    }

    private suspend fun loadFromLocal(month: String, previousMonth: String) {
        combine(
            incomeRepository.getTotalIncomeByMonthFlow(month),
            expenseRepository.getTotalExpensesByMonthFlow(month),
            loanRepository.getTotalRemainingAmount()
        ) { monthlyIncome, monthlyExpenses, loans ->
            
            val totalLoans = loans ?: 0.0
            val balance = monthlyIncome - monthlyExpenses
            val savings = balance.coerceAtLeast(0.0)
            
            // Calculer le pourcentage d'utilisation
            val usagePercent = if (monthlyIncome > 0) {
                ((monthlyExpenses / monthlyIncome) * 100).toInt().coerceAtMost(999)
            } else 0
            
            // Déterminer le niveau de gestion
            val managementLevel = when {
                usagePercent > 100 -> ManagementLevel.BAD
                usagePercent > 80 -> ManagementLevel.WARNING
                else -> ManagementLevel.GOOD
            }

            DashboardUiState(
                balance = balance,
                totalIncome = monthlyIncome,
                totalExpenses = monthlyExpenses,
                totalSavings = savings,
                totalLoans = totalLoans,
                currentMonth = month,
                hasSalary = monthlyIncome > 0,
                usagePercent = usagePercent,
                managementLevel = managementLevel,
                isLoading = false,
                useRemote = false
            )
        }.collect { state ->
            // Vérifier si salaire existe et charger mois précédent
            val hasSalary = incomeRepository.hasSalaryForMonth(month)
            val prevIncome = incomeRepository.getTotalIncomeByMonth(previousMonth)
            val prevExpenses = expenseRepository.getTotalExpensesByMonth(previousMonth)
            
            _uiState.value = state.copy(
                hasSalary = hasSalary,
                previousMonthIncome = prevIncome,
                previousMonthExpenses = prevExpenses,
                previousMonthBalance = prevIncome - prevExpenses
            )
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }
    
    fun toggleDataSource() {
        _uiState.value = _uiState.value.copy(useRemote = !_uiState.value.useRemote)
        loadDashboardData()
    }
}

