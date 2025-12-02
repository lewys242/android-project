package com.mbongo.app.data.repository

import com.mbongo.app.data.local.dao.ExpenseDao
import com.mbongo.app.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    
    fun getExpensesByMonth(month: String): Flow<List<Expense>> = expenseDao.getExpensesByMonth(month)
    
    fun getExpensesByCategory(categoryId: Long): Flow<List<Expense>> = 
        expenseDao.getExpensesByCategory(categoryId)
    
    fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<Expense>> =
        expenseDao.getExpensesByDateRange(startDate, endDate)
    
    fun getTotalExpenses(): Flow<Double?> = expenseDao.getTotalExpenses()
    
    fun getTotalExpensesByMonthFlow(month: String): Flow<Double> = expenseDao.getTotalExpensesByMonthFlow(month)
    
    suspend fun getTotalExpensesByMonth(month: String): Double = expenseDao.getTotalExpensesByMonth(month)
    
    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)
    
    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
}
