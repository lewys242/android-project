package com.mbongo.app.data.repository

import com.mbongo.app.data.local.dao.LoanDao
import com.mbongo.app.data.local.entity.Loan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoanRepository @Inject constructor(
    private val loanDao: LoanDao
) {
    fun getAllLoans(): Flow<List<Loan>> = loanDao.getAllLoans()
    
    fun getActiveLoans(): Flow<List<Loan>> = loanDao.getActiveLoans()
    
    fun getTotalLoanAmount(): Flow<Double?> = loanDao.getTotalLoanAmount()
    
    fun getTotalRemainingAmount(): Flow<Double?> = loanDao.getTotalRemainingAmount()
    
    suspend fun getLoanById(id: Long): Loan? = loanDao.getLoanById(id)
    
    suspend fun insertLoan(loan: Loan): Long = loanDao.insertLoan(loan)
    
    suspend fun updateLoan(loan: Loan) = loanDao.updateLoan(loan)
    
    suspend fun deleteLoan(loan: Loan) = loanDao.deleteLoan(loan)
}
