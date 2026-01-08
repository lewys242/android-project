package com.mbongo.app.data.repository

import com.mbongo.app.data.local.dao.IncomeDao
import com.mbongo.app.data.local.entity.Income
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao
) {
    fun getAllIncomes(): Flow<List<Income>> = incomeDao.getAllIncomes()
    
    fun getIncomesByMonth(month: String): Flow<List<Income>> = incomeDao.getIncomesByMonth(month)
    
    fun getIncomesByCategory(categoryId: Long): Flow<List<Income>> = 
        incomeDao.getIncomesByCategory(categoryId)
    
    fun getIncomesByDateRange(startMonth: String, endMonth: String): Flow<List<Income>> =
        incomeDao.getIncomesByDateRange(startMonth, endMonth)
    
    fun getTotalIncomes(): Flow<Double?> = incomeDao.getTotalIncomes()
    
    fun getTotalIncomeByMonthFlow(month: String): Flow<Double> = incomeDao.getTotalIncomeByMonthFlow(month)
    
    suspend fun getTotalIncomeByMonth(month: String): Double = incomeDao.getTotalIncomeByMonth(month)
    
    suspend fun hasSalaryForMonth(month: String): Boolean = incomeDao.hasSalaryForMonth(month) > 0
    
    suspend fun getSalaryTotalByMonth(month: String): Double = incomeDao.getSalaryTotalByMonth(month)
    
    suspend fun getIncomeById(id: Long): Income? = incomeDao.getIncomeById(id)
    
    suspend fun insertIncome(income: Income): Long = incomeDao.insertIncome(income)
    
    suspend fun updateIncome(income: Income) = incomeDao.updateIncome(income)
    
    suspend fun deleteIncome(income: Income) = incomeDao.deleteIncome(income)
    
    // ===== Méthodes pour la synchronisation =====
    
    suspend fun getUnsyncedIncomes(): List<Income> = incomeDao.getUnsyncedIncomes()
    
    suspend fun getPendingDeleteIncomes(): List<Income> = incomeDao.getPendingDeleteIncomes()
    
    suspend fun markAsSynced(localId: Long, remoteId: Long) = incomeDao.markAsSynced(localId, remoteId)
    
    suspend fun markForDeletion(id: Long) = incomeDao.markForDeletion(id)
    
    suspend fun deleteSyncedPendingDelete() = incomeDao.deleteSyncedPendingDelete()
}
