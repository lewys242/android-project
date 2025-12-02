package com.mbongo.app.data.local.dao

import androidx.room.*
import com.mbongo.app.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("""
        SELECT * FROM expenses 
        WHERE strftime('%Y-%m', date) = :month 
        ORDER BY date DESC
    """)
    fun getExpensesByMonth(month: String): Flow<List<Expense>>

    @Query("""
        SELECT * FROM expenses 
        WHERE strftime('%Y', date) = :year 
        ORDER BY date DESC
    """)
    fun getExpensesByYear(year: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(categoryId: Long): Flow<List<Expense>>

    @Query("""
        SELECT * FROM expenses 
        WHERE categoryId = :categoryId 
        AND strftime('%Y-%m', date) = :month 
        ORDER BY date DESC
    """)
    fun getExpensesByCategoryAndMonth(categoryId: Long, month: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE strftime('%Y-%m', date) = :month")
    suspend fun getTotalExpensesByMonth(month: String): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE strftime('%Y', date) = :year")
    suspend fun getTotalExpensesByYear(year: String): Double

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses")
    fun getTotalExpenses(): Flow<Double?>

    // Total dépenses par mois avec Flow
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE strftime('%Y-%m', date) = :month")
    fun getTotalExpensesByMonthFlow(month: String): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)
}
