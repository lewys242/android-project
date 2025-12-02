package com.mbongo.app.data.repository

import com.mbongo.app.data.local.dao.RepaymentDao
import com.mbongo.app.data.local.entity.Repayment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepaymentRepository @Inject constructor(
    private val repaymentDao: RepaymentDao
) {
    fun getRepaymentsByLoan(loanId: Long): Flow<List<Repayment>> = 
        repaymentDao.getRepaymentsByLoan(loanId)
    
    fun getTotalRepaidForLoan(loanId: Long): Flow<Double?> = 
        repaymentDao.getTotalRepaidForLoan(loanId)
    
    suspend fun getRepaymentById(id: Long): Repayment? = repaymentDao.getRepaymentById(id)
    
    suspend fun insertRepayment(repayment: Repayment): Long = repaymentDao.insertRepayment(repayment)
    
    suspend fun updateRepayment(repayment: Repayment) = repaymentDao.updateRepayment(repayment)
    
    suspend fun deleteRepayment(repayment: Repayment) = repaymentDao.deleteRepayment(repayment)
}
