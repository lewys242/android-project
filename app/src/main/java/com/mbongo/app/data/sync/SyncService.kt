package com.mbongo.app.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.mbongo.app.data.local.dao.IncomeDao
import com.mbongo.app.data.local.entity.Income
import com.mbongo.app.data.remote.ApiService
import com.mbongo.app.data.remote.dto.CreateIncomeDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR,
    OFFLINE
}

data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTime: Long? = null,
    val pendingCount: Int = 0,
    val errorMessage: String? = null
)

@Singleton
class SyncService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val incomeDao: IncomeDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "SyncService"
    }
    
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    private val _isServerAvailable = MutableStateFlow(false)
    val isServerAvailable: StateFlow<Boolean> = _isServerAvailable.asStateFlow()
    
    /**
     * Vérifie si le réseau est disponible
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    /**
     * Vérifie si le serveur est accessible
     */
    suspend fun checkServerAvailability(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!isNetworkAvailable()) {
                    _isServerAvailable.value = false
                    return@withContext false
                }
                
                // Tente un appel simple au serveur
                val response = apiService.getIncomes(null)
                val available = response.isSuccessful
                _isServerAvailable.value = available
                Log.d(TAG, "Server availability: $available")
                available
            } catch (e: Exception) {
                Log.e(TAG, "Server not available: ${e.message}")
                _isServerAvailable.value = false
                false
            }
        }
    }
    
    /**
     * Synchronise tous les revenus non synchronisés avec le serveur
     */
    suspend fun syncIncomes(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Vérifie d'abord si le serveur est disponible
                if (!checkServerAvailability()) {
                    _syncState.value = SyncState(
                        status = SyncStatus.OFFLINE,
                        pendingCount = incomeDao.getUnsyncedIncomes().size
                    )
                    return@withContext false
                }
                
                _syncState.value = _syncState.value.copy(status = SyncStatus.SYNCING)
                
                var success = true
                
                // 1. Envoyer les nouveaux revenus non synchronisés
                val unsyncedIncomes = incomeDao.getUnsyncedIncomes()
                Log.d(TAG, "Found ${unsyncedIncomes.size} unsynced incomes")
                
                for (income in unsyncedIncomes) {
                    try {
                        val dto = CreateIncomeDto(
                            amount = income.amount,
                            description = income.description,
                            month = income.month,
                            date = income.date,
                            type = income.type,
                            categoryId = null
                        )
                        
                        val response = apiService.createIncome(dto)
                        if (response.isSuccessful && response.body() != null) {
                            val remoteId = response.body()!!.id
                            incomeDao.markAsSynced(income.id, remoteId)
                            Log.d(TAG, "Income ${income.id} synced with remote ID $remoteId")
                        } else {
                            Log.e(TAG, "Failed to sync income ${income.id}: ${response.message()}")
                            success = false
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error syncing income ${income.id}", e)
                        success = false
                    }
                }
                
                // 2. Supprimer sur le serveur les revenus marqués pour suppression
                val pendingDeletes = incomeDao.getPendingDeleteIncomes()
                Log.d(TAG, "Found ${pendingDeletes.size} incomes pending deletion")
                
                for (income in pendingDeletes) {
                    if (income.remoteId != null) {
                        try {
                            val response = apiService.deleteIncome(income.remoteId)
                            if (response.isSuccessful) {
                                // Supprime définitivement de la base locale
                                incomeDao.deleteIncomeById(income.id)
                                Log.d(TAG, "Income ${income.id} deleted from server")
                            } else {
                                Log.e(TAG, "Failed to delete income ${income.id} from server")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error deleting income ${income.id} from server", e)
                        }
                    } else {
                        // Pas de remoteId, on peut supprimer directement
                        incomeDao.deleteIncomeById(income.id)
                    }
                }
                
                // Nettoyer les suppressions synchronisées
                incomeDao.deleteSyncedPendingDelete()
                
                val remainingUnsynced = incomeDao.getUnsyncedIncomes().size
                _syncState.value = SyncState(
                    status = if (success) SyncStatus.SUCCESS else SyncStatus.ERROR,
                    lastSyncTime = System.currentTimeMillis(),
                    pendingCount = remainingUnsynced,
                    errorMessage = if (!success) "Certains éléments n'ont pas pu être synchronisés" else null
                )
                
                Log.d(TAG, "Sync completed. Success: $success, Remaining unsynced: $remainingUnsynced")
                success
            } catch (e: Exception) {
                Log.e(TAG, "Sync error", e)
                _syncState.value = SyncState(
                    status = SyncStatus.ERROR,
                    errorMessage = e.localizedMessage
                )
                false
            }
        }
    }
    
    /**
     * Récupère le nombre d'éléments en attente de synchronisation
     */
    suspend fun getPendingCount(): Int {
        return withContext(Dispatchers.IO) {
            incomeDao.getUnsyncedIncomes().size + incomeDao.getPendingDeleteIncomes().size
        }
    }
}
