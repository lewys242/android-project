package com.mbongo.app.data.local.dao

import androidx.room.*
import com.mbongo.app.data.local.entity.Repayment
import kotlinx.coroutines.flow.Flow

@Dao
interface RepaymentDao {
    @Query("SELECT * FROM repayments ORDER BY date DESC")
    fun getAllRepayments(): Flow<List<Repayment>>

    @Query("SELECT * FROM repayments WHERE loanId = :loanId ORDER BY date DESC")
    fun getRepaymentsByLoan(loanId: Long): Flow<List<Repayment>>

    @Query("SELECT * FROM repayments WHERE id = :id")
    suspend fun getRepaymentById(id: Long): Repayment?

    @Query("SELECT COALESCE(SUM(principalAmount), 0) FROM repayments WHERE loanId = :loanId")
    suspend fun getTotalPrincipalPaid(loanId: Long): Double

    @Query("SELECT COALESCE(SUM(interestAmount), 0) FROM repayments WHERE loanId = :loanId")
    suspend fun getTotalInterestPaid(loanId: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM repayments WHERE loanId = :loanId")
    fun getTotalRepaidForLoan(loanId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepayment(repayment: Repayment): Long

    @Update
    suspend fun updateRepayment(repayment: Repayment)

    @Delete
    suspend fun deleteRepayment(repayment: Repayment)

    @Query("DELETE FROM repayments WHERE id = :id")
    suspend fun deleteRepaymentById(id: Long)
}
