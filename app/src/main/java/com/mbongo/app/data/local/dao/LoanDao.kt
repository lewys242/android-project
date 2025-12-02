package com.mbongo.app.data.local.dao

import androidx.room.*
import com.mbongo.app.data.local.entity.Loan
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY startDate DESC")
    fun getAllLoans(): Flow<List<Loan>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoanById(id: Long): Loan?

    @Query("SELECT * FROM loans ORDER BY startDate DESC")
    fun getActiveLoans(): Flow<List<Loan>>

    @Query("SELECT COALESCE(SUM(principal), 0) FROM loans")
    fun getTotalLoanAmount(): Flow<Double?>

    @Query("SELECT COALESCE(SUM(principal), 0) FROM loans")
    fun getTotalRemainingAmount(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: Loan): Long

    @Update
    suspend fun updateLoan(loan: Loan)

    @Delete
    suspend fun deleteLoan(loan: Loan)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteLoanById(id: Long)
}
