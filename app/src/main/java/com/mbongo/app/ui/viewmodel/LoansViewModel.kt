package com.mbongo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Loan
import com.mbongo.app.data.local.entity.Repayment
import com.mbongo.app.data.repository.LoanRepository
import com.mbongo.app.data.repository.RepaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoansViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val repaymentRepository: RepaymentRepository
) : ViewModel() {

    val loans: StateFlow<List<Loan>> = loanRepository.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeLoans: StateFlow<List<Loan>> = loanRepository.getActiveLoans()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalLoanAmount: StateFlow<Double> = loanRepository.getTotalLoanAmount()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalRemainingAmount: StateFlow<Double> = loanRepository.getTotalRemainingAmount()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun addLoan(loan: Loan) {
        viewModelScope.launch {
            loanRepository.insertLoan(loan)
        }
    }

    fun updateLoan(loan: Loan) {
        viewModelScope.launch {
            loanRepository.updateLoan(loan)
        }
    }

    fun deleteLoan(loan: Loan) {
        viewModelScope.launch {
            loanRepository.deleteLoan(loan)
        }
    }

    fun addRepayment(repayment: Repayment, loan: Loan) {
        viewModelScope.launch {
            repaymentRepository.insertRepayment(repayment)
            // Note: Loan model doesn't track remaining amount yet
            // This would need to be implemented in future versions
        }
    }

    fun getRepaymentsByLoan(loanId: Long): Flow<List<Repayment>> {
        return repaymentRepository.getRepaymentsByLoan(loanId)
    }
}
