package com.mbongo.app.data.local.dao

import androidx.room.*
import com.mbongo.app.data.local.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes ORDER BY month DESC")
    fun getAllIncomes(): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE month = :month ORDER BY month DESC")
    fun getIncomesByMonth(month: String): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE id = :id")
    suspend fun getIncomeById(id: Long): Income?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE month = :month")
    suspend fun getTotalIncomeByMonth(month: String): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE substr(month, 1, 4) = :year")
    suspend fun getTotalIncomeByYear(year: String): Double

    @Query("SELECT * FROM incomes WHERE id = :categoryId ORDER BY month DESC")
    fun getIncomesByCategory(categoryId: Long): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE month >= :startMonth AND month <= :endMonth ORDER BY month DESC")
    fun getIncomesByDateRange(startMonth: String, endMonth: String): Flow<List<Income>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes")
    fun getTotalIncomes(): Flow<Double?>

    // Vérifier si un salaire existe pour un mois donné
    @Query("SELECT COUNT(*) FROM incomes WHERE month = :month AND (type = 'salary' OR LOWER(description) LIKE '%salaire%')")
    suspend fun hasSalaryForMonth(month: String): Int

    // Obtenir le total des salaires pour un mois
    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE month = :month AND (type = 'salary' OR LOWER(description) LIKE '%salaire%')")
    suspend fun getSalaryTotalByMonth(month: String): Double

    // Obtenir les revenus du mois avec Flow
    @Query("SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE month = :month")
    fun getTotalIncomeByMonthFlow(month: String): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income): Long

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("DELETE FROM incomes WHERE id = :id")
    suspend fun deleteIncomeById(id: Long)
}
