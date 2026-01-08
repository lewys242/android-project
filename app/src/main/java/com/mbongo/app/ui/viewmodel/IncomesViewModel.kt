package com.mbongo.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Income
import com.mbongo.app.data.local.entity.Category
import com.mbongo.app.data.repository.IncomeRepository
import com.mbongo.app.data.repository.CategoryRepository
import com.mbongo.app.data.sync.SyncService
import com.mbongo.app.data.sync.SyncState
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
    val type: String?,
    val isSynced: Boolean = false
)

@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val categoryRepository: CategoryRepository,
    private val syncService: SyncService
) : ViewModel() {
    
    companion object {
        private const val TAG = "IncomesViewModel"
    }
    
    private val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val _currentMonth = MutableStateFlow(dateFormat.format(Date()))
    val currentMonth: StateFlow<String> = _currentMonth.asStateFlow()

    private val _incomes = MutableStateFlow<List<IncomeDisplay>>(emptyList())
    val incomes: StateFlow<List<IncomeDisplay>> = _incomes.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesByType("income")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _totalIncomes = MutableStateFlow(0.0)
    val totalIncomes: StateFlow<Double> = _totalIncomes.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // État de synchronisation
    val syncState: StateFlow<SyncState> = syncService.syncState
    val isServerAvailable: StateFlow<Boolean> = syncService.isServerAvailable

    init {
        loadIncomes()
        // Tenter une synchronisation au démarrage
        attemptSync()
    }
    
    private fun loadIncomes() {
        viewModelScope.launch {
            _isLoading.value = true
            loadIncomesFromLocal()
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
                        type = income.type,
                        isSynced = income.isSynced
                    )
                }
                _totalIncomes.value = localIncomes.sumOf { it.amount }
                _isLoading.value = false
            }
        }
    }

    fun addIncome(income: Income) {
        viewModelScope.launch {
            // Sauvegarder en local (non synchronisé par défaut)
            val newIncome = income.copy(isSynced = false)
            incomeRepository.insertIncome(newIncome)
            Log.d(TAG, "Income added locally: ${income.description}, ${income.amount}")
            
            // Tenter de synchroniser avec le serveur
            attemptSync()
        }
    }

    fun updateIncome(income: Income) {
        viewModelScope.launch {
            // Marquer comme non synchronisé après modification
            val updatedIncome = income.copy(isSynced = false)
            incomeRepository.updateIncome(updatedIncome)
            
            // Tenter de synchroniser
            attemptSync()
        }
    }

    fun deleteIncome(income: IncomeDisplay) {
        viewModelScope.launch {
            // Marquer pour suppression (soft delete pour la sync)
            incomeRepository.markForDeletion(income.id)
            Log.d(TAG, "Income marked for deletion: ${income.id}")
            
            // Tenter de synchroniser
            attemptSync()
        }
    }
    
    /**
     * Tente de synchroniser les données avec le serveur
     */
    fun attemptSync() {
        viewModelScope.launch {
            Log.d(TAG, "Attempting sync...")
            val success = syncService.syncIncomes()
            if (success) {
                Log.d(TAG, "Sync successful")
            } else {
                Log.d(TAG, "Sync failed or offline - data saved locally")
            }
        }
    }
    
    /**
     * Force une synchronisation manuelle
     */
    fun forceSync() {
        viewModelScope.launch {
            _isLoading.value = true
            syncService.syncIncomes()
            _isLoading.value = false
        }
    }
    
    fun refresh() {
        loadIncomes()
        attemptSync()
    }
}
