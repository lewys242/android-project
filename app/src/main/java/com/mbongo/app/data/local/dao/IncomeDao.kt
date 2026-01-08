package com.mbongo.app.data.local.dao

import androidx.room.*
import com.mbongo.app.data.local.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes WHERE pendingDelete = 0 ORDER BY month DESC")
    fun getAllIncomes(): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE month = :month AND pendingDelete = 0 ORDER BY month DESC")
    fun getIncomesByMonth(month: String): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE id = :id")
    suspend fun getIncomeById(id: Long): Income?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE month = :month AND pendingDelete = 0")
    suspend fun getTotalIncomeByMonth(month: String): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE substr(month, 1, 4) = :year AND pendingDelete = 0")
    suspend fun getTotalIncomeByYear(year: String): Double

    @Query("SELECT * FROM incomes WHERE id = :categoryId AND pendingDelete = 0 ORDER BY month DESC")
    fun getIncomesByCategory(categoryId: Long): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE month >= :startMonth AND month <= :endMonth AND pendingDelete = 0 ORDER BY month DESC")
    fun getIncomesByDateRange(startMonth: String, endMonth: String): Flow<List<Income>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE pendingDelete = 0")
    fun getTotalIncomes(): Flow<Double?>

    // Vérifier si un revenu existe pour un mois donné
    @Query("SELECT COUNT(*) FROM incomes WHERE month = :month AND pendingDelete = 0")
    suspend fun hasSalaryForMonth(month: String): Int

    // Obtenir le total des revenus pour un mois
    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE month = :month AND pendingDelete = 0")
    suspend fun getSalaryTotalByMonth(month: String): Double

    // Obtenir les revenus du mois avec Flow
    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE month = :month AND pendingDelete = 0")
    fun getTotalIncomeByMonthFlow(month: String): Flow<Double>
    
    // ===== Méthodes pour la synchronisation =====
    
    // Obtenir les revenus non synchronisés
    @Query("SELECT * FROM incomes WHERE isSynced = 0 AND pendingDelete = 0")
    suspend fun getUnsyncedIncomes(): List<Income>
    
    // Obtenir les revenus marqués pour suppression
    @Query("SELECT * FROM incomes WHERE pendingDelete = 1")
    suspend fun getPendingDeleteIncomes(): List<Income>
    
    // Marquer un revenu comme synchronisé
    @Query("UPDATE incomes SET isSynced = 1, remoteId = :remoteId WHERE id = :localId")
    suspend fun markAsSynced(localId: Long, remoteId: Long)
    
    // Marquer pour suppression (soft delete)
    @Query("UPDATE incomes SET pendingDelete = 1 WHERE id = :id")
    suspend fun markForDeletion(id: Long)
    
    // Supprimer définitivement les revenus synchronisés et marqués pour suppression
    @Query("DELETE FROM incomes WHERE pendingDelete = 1 AND isSynced = 1")
    suspend fun deleteSyncedPendingDelete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income): Long

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("DELETE FROM incomes WHERE id = :id")
    suspend fun deleteIncomeById(id: Long)
}
