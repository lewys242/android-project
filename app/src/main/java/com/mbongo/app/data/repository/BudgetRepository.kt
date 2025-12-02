package com.mbongo.app.data.repository

import com.mbongo.app.data.local.dao.BudgetDao
import com.mbongo.app.data.local.entity.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {
    fun getAllBudgets(): Flow<List<Budget>> = budgetDao.getAllBudgets()
    
    fun getActiveBudgets(): Flow<List<Budget>> = budgetDao.getActiveBudgets()
    
    fun getBudgetsByCategory(categoryId: Long): Flow<List<Budget>> = 
        budgetDao.getBudgetsByCategory(categoryId)
    
    fun getTotalBudgetAmount(): Flow<Double?> = budgetDao.getTotalBudgetAmount()
    
    suspend fun getBudgetById(id: Long): Budget? = budgetDao.getBudgetById(id)
    
    suspend fun insertBudget(budget: Budget): Long = budgetDao.insertBudget(budget)
    
    suspend fun updateBudget(budget: Budget) = budgetDao.updateBudget(budget)
    
    suspend fun deleteBudget(budget: Budget) = budgetDao.deleteBudget(budget)
}
