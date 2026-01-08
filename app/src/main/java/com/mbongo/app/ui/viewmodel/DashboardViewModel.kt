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
    val previousMonthHasSalary: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val useRemote: Boolean = false, // Utiliser les données locales par défaut (offline-first)
    val savingsRate: Int = 10, // Taux d'épargne: 5% ou 10%
    val savingsEnabled: Boolean = true, // Épargne activée par défaut
    val showBalance: Boolean = true, // Afficher/masquer le solde (comme web)
    val showAdvice: Boolean = false, // Afficher les conseils
    val showPreviousMonth: Boolean = false, // Afficher récap mois précédent
    val transactionCount: Int = 0 // Nombre de transactions
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
        Log.d("DashboardViewModel", "Loading from remote for month: $month ($monthNum/$year)")
        
        try {
            // Charger les revenus, dépenses et prêts en parallèle
            var totalIncome = 0.0
            var totalExpenses = 0.0
            var totalLoans = 0.0
            var hasError = false
            var errorMessage = ""
            
            // Charger les revenus pour le mois
            remoteRepository.getIncomes(month).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        totalIncome = result.data.sumOf { it.amount }
                        Log.d("DashboardViewModel", "Loaded incomes: $totalIncome FCFA (${result.data.size} revenus)")
                    }
                    is ApiResult.Error -> {
                        hasError = true
                        errorMessage = result.message
                        Log.e("DashboardViewModel", "Error loading incomes: ${result.message}")
                    }
                    is ApiResult.Loading -> {}
                }
            }
            
            // Charger les dépenses pour le mois
            remoteRepository.getExpenses(monthNum, year).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        totalExpenses = result.data.sumOf { it.amount }
                        Log.d("DashboardViewModel", "Loaded expenses: $totalExpenses FCFA (${result.data.size} dépenses)")
                    }
                    is ApiResult.Error -> {
                        hasError = true
                        errorMessage = result.message
                        Log.e("DashboardViewModel", "Error loading expenses: ${result.message}")
                    }
                    is ApiResult.Loading -> {}
                }
            }
            
            // Charger les prêts
            remoteRepository.getLoans().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        totalLoans = result.data.sumOf { loan ->
                            loan.remaining ?: (loan.principal + loan.principal * (loan.interestRate / 100) - (loan.totalRepaid ?: 0.0))
                        }
                        Log.d("DashboardViewModel", "Loaded loans: $totalLoans FCFA (${result.data.size} prêts)")
                    }
                    is ApiResult.Error -> {
                        Log.e("DashboardViewModel", "Error loading loans: ${result.message}")
                    }
                    is ApiResult.Loading -> {}
                }
            }
            
            if (hasError && totalIncome == 0.0 && totalExpenses == 0.0) {
                // Fallback vers local si erreur totale
                Log.w("DashboardViewModel", "Falling back to local data")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Connexion au serveur impossible: $errorMessage"
                )
                loadFromLocal(month, previousMonth)
                return
            }
            
            // Calculer les valeurs dérivées
            val balance = totalIncome - totalExpenses
            val usagePercent = if (totalIncome > 0) {
                ((totalExpenses / totalIncome) * 100).toInt().coerceAtMost(999)
            } else 0
            
            val managementLevel = when {
                usagePercent > 100 -> ManagementLevel.BAD
                usagePercent > 80 -> ManagementLevel.WARNING
                else -> ManagementLevel.GOOD
            }
            
            Log.d("DashboardViewModel", "Final: income=$totalIncome, expenses=$totalExpenses, balance=$balance, loans=$totalLoans")
            
            _uiState.value = _uiState.value.copy(
                balance = balance,
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                totalSavings = balance.coerceAtLeast(0.0),
                totalLoans = totalLoans,
                currentMonth = month,
                hasSalary = totalIncome > 0,
                usagePercent = usagePercent,
                managementLevel = managementLevel,
                isLoading = false,
                error = null
            )
            
        } catch (e: Exception) {
            Log.e("DashboardViewModel", "Exception: ${e.message}")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Erreur: ${e.message}"
            )
            loadFromLocal(month, previousMonth)
        }
    }
    
    private suspend fun loadLoansFromRemote() {
        // Cette méthode n'est plus nécessaire car les prêts sont chargés dans loadFromRemote
    }

    private suspend fun loadFromLocal(month: String, previousMonth: String) {
        combine(
            incomeRepository.getTotalIncomeByMonthFlow(month),
            expenseRepository.getTotalExpensesByMonthFlow(month),
            loanRepository.getTotalRemainingAmount()
        ) { monthlyIncome, monthlyExpenses, loans ->
            
            val totalLoans = loans ?: 0.0
            val balance = monthlyIncome - monthlyExpenses
            // L'épargne est calculée sur le pourcentage du revenu, pas le solde
            val currentRate = _uiState.value.savingsRate
            val savings = monthlyIncome * (currentRate / 100.0)
            
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
                useRemote = false,
                savingsRate = currentRate,
                savingsEnabled = _uiState.value.savingsEnabled
            )
        }.collect { state ->
            // Vérifier si salaire existe et charger mois précédent
            val hasSalary = incomeRepository.hasSalaryForMonth(month)
            val prevIncome = incomeRepository.getTotalIncomeByMonth(previousMonth)
            val prevExpenses = expenseRepository.getTotalExpensesByMonth(previousMonth)
            val prevHasSalary = incomeRepository.hasSalaryForMonth(previousMonth)
            
            // Compter les transactions (dépenses) du mois
            val expenseCount = expenseRepository.getExpenseCountByMonth(month)
            
            _uiState.value = state.copy(
                hasSalary = hasSalary,
                previousMonthIncome = prevIncome,
                previousMonthExpenses = prevExpenses,
                previousMonthBalance = prevIncome - prevExpenses,
                previousMonthHasSalary = prevHasSalary,
                transactionCount = expenseCount,
                // Calculer l'épargne basée sur le pourcentage du revenu
                totalSavings = state.totalIncome * (_uiState.value.savingsRate / 100.0),
                // Conserver les préférences d'affichage
                showBalance = _uiState.value.showBalance,
                showAdvice = _uiState.value.showAdvice,
                showPreviousMonth = _uiState.value.showPreviousMonth
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
    
    fun setSavingsRate(rate: Int) {
        _uiState.value = _uiState.value.copy(
            savingsRate = rate,
            totalSavings = _uiState.value.totalIncome * (rate / 100.0)
        )
    }
    
    fun setSavingsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(savingsEnabled = enabled)
    }
    
    fun saveNow() {
        // TODO: Enregistrer l'épargne comme une transaction
        viewModelScope.launch {
            val savingsAmount = _uiState.value.totalIncome * (_uiState.value.savingsRate / 100.0)
            // Pour l'instant, on pourrait créer une dépense "Épargne" ou une entité Savings
            Log.d("DashboardViewModel", "Saving $savingsAmount FCFA")
        }
    }
    
    fun toggleBalanceVisibility() {
        _uiState.value = _uiState.value.copy(showBalance = !_uiState.value.showBalance)
    }
    
    fun toggleAdvice() {
        _uiState.value = _uiState.value.copy(showAdvice = !_uiState.value.showAdvice)
    }
    
    fun togglePreviousMonth() {
        _uiState.value = _uiState.value.copy(showPreviousMonth = !_uiState.value.showPreviousMonth)
    }
    
    // Obtenir les conseils selon le niveau de gestion (comme dans le web)
    fun getManagementAdvice(): List<String> {
        return when (_uiState.value.managementLevel) {
            ManagementLevel.BAD -> listOf(
                "Identifiez les dépenses non essentielles à réduire",
                "Reportez les achats non urgents au mois prochain",
                "Cherchez des sources de revenus complémentaires"
            )
            ManagementLevel.WARNING -> listOf(
                "Surveillez vos postes de dépenses cette fin de mois",
                "Évitez les achats impulsifs",
                "Gardez une marge pour les imprévus"
            )
            ManagementLevel.GOOD -> listOf(
                "Continuez ainsi ! Pensez à épargner le surplus",
                "Profitez-en pour constituer un fond d'urgence",
                "Vous pouvez vous faire un petit plaisir raisonnable 🎁"
            )
        }
    }
    
    fun getManagementTitle(): String {
        return when (_uiState.value.managementLevel) {
            ManagementLevel.BAD -> "🚨 Vous dépensez plus que vous gagnez !"
            ManagementLevel.WARNING -> "⚡ Vous approchez de votre limite"
            ManagementLevel.GOOD -> "🎉 Excellent ! Vous gérez bien votre budget"
        }
    }
}

