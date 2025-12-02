package com.mbongo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Loan
import com.mbongo.app.data.local.entity.Repayment
import com.mbongo.app.data.repository.LoanRepository
import com.mbongo.app.data.repository.RepaymentRepository
import com.mbongo.app.ui.screens.loans.LoanBreakdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
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

    // Cache des remboursements par prêt
    private val _repaymentsByLoan = MutableStateFlow<Map<Long, List<Repayment>>>(emptyMap())
    
    init {
        // Charger tous les remboursements au démarrage
        viewModelScope.launch {
            loans.collect { loanList ->
                loanList.forEach { loan ->
                    repaymentRepository.getRepaymentsByLoan(loan.id).collect { repayments ->
                        _repaymentsByLoan.value = _repaymentsByLoan.value + (loan.id to repayments)
                    }
                }
            }
        }
    }

    fun calculateLoanBreakdown(loan: Loan): LoanBreakdown {
        val principal = loan.principal
        val interestRate = loan.interestRate
        val totalInterest = principal * (interestRate / 100)
        val totalDue = principal + totalInterest
        
        // Récupérer les remboursements depuis le cache
        val repayments = _repaymentsByLoan.value[loan.id] ?: emptyList()
        
        val interestPaid = repayments.sumOf { it.interestAmount }
        val principalPaid = repayments.sumOf { it.principalAmount }
        
        val interestRemaining = (totalInterest - interestPaid).coerceAtLeast(0.0)
        val principalRemaining = (principal - principalPaid).coerceAtLeast(0.0)
        
        val totalPaid = interestPaid + principalPaid
        val progress = if (totalDue > 0) (totalPaid / totalDue).toFloat().coerceIn(0f, 1f) else 0f
        
        return LoanBreakdown(
            totalInterest = totalInterest,
            interestRemaining = interestRemaining,
            principalRemaining = principalRemaining,
            interestPaid = interestPaid,
            principalPaid = principalPaid,
            totalDue = totalDue,
            progress = progress
        )
    }

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

    fun addRepayment(loan: Loan, interestAmount: Double, principalAmount: Double) {
        viewModelScope.launch {
            val repayment = Repayment(
                loanId = loan.id,
                amount = interestAmount + principalAmount,
                interestAmount = interestAmount,
                principalAmount = principalAmount,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            repaymentRepository.insertRepayment(repayment)
            
            // Rafraîchir le cache des remboursements
            repaymentRepository.getRepaymentsByLoan(loan.id).collect { repayments ->
                _repaymentsByLoan.value = _repaymentsByLoan.value + (loan.id to repayments)
            }
        }
    }

    fun addRepayment(repayment: Repayment, loan: Loan) {
        viewModelScope.launch {
            repaymentRepository.insertRepayment(repayment)
        }
    }

    fun getRepaymentsByLoan(loanId: Long): Flow<List<Repayment>> {
        return repaymentRepository.getRepaymentsByLoan(loanId)
    }
}
