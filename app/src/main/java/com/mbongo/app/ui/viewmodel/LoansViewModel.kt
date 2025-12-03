package com.mbongo.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbongo.app.data.local.entity.Loan
import com.mbongo.app.data.local.entity.Repayment
import com.mbongo.app.data.repository.LoanRepository
import com.mbongo.app.data.repository.RepaymentRepository
import com.mbongo.app.data.repository.RemoteRepository
import com.mbongo.app.data.repository.ApiResult
import com.mbongo.app.data.remote.dto.CreateLoanDto
import com.mbongo.app.data.remote.dto.CreateRepaymentDto
import com.mbongo.app.ui.screens.loans.LoanBreakdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// Modèle unifié pour affichage des prêts
data class LoanDisplay(
    val id: Long,
    val principal: Double,
    val interestRate: Double,
    val termMonths: Int?,
    val startDate: String?,
    val lender: String?,
    val purpose: String?,
    val totalRepaid: Double,
    val interestTotal: Double,
    val interestPaid: Double,
    val principalPaid: Double,
    val interestRemaining: Double,
    val principalRemaining: Double,
    val totalDue: Double,
    val balance: Double
) {
    // Propriétés calculées pour compatibilité avec l'UI
    val totalInterest: Double get() = interestTotal
    val progress: Float get() = if (totalDue > 0) ((totalRepaid) / totalDue).toFloat().coerceIn(0f, 1f) else 0f
}

@HiltViewModel
class LoansViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val repaymentRepository: RepaymentRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {
    
    private val _useRemote = MutableStateFlow(true)

    private val _loans = MutableStateFlow<List<LoanDisplay>>(emptyList())
    val loans: StateFlow<List<LoanDisplay>> = _loans.asStateFlow()

    private val _totalLoanAmount = MutableStateFlow(0.0)
    val totalLoanAmount: StateFlow<Double> = _totalLoanAmount.asStateFlow()

    private val _totalRemainingAmount = MutableStateFlow(0.0)
    val totalRemainingAmount: StateFlow<Double> = _totalRemainingAmount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Cache des remboursements par prêt
    private val _repaymentsByLoan = MutableStateFlow<Map<Long, List<Repayment>>>(emptyMap())
    
    init {
        loadLoans()
    }
    
    private fun loadLoans() {
        viewModelScope.launch {
            _isLoading.value = true
            
            if (_useRemote.value) {
                loadLoansFromRemote()
            } else {
                loadLoansFromLocal()
            }
        }
    }
    
    private suspend fun loadLoansFromRemote() {
        remoteRepository.getLoans().collect { result ->
            when (result) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _loans.value = result.data.map { dto ->
                        LoanDisplay(
                            id = dto.id,
                            principal = dto.principal,
                            interestRate = dto.interestRate,
                            termMonths = dto.termMonths,
                            startDate = dto.startDate,
                            lender = dto.lender,
                            purpose = dto.purpose,
                            totalRepaid = dto.totalRepaid ?: 0.0,
                            interestTotal = dto.principal * (dto.interestRate / 100),
                            interestPaid = 0.0, // Calculé depuis API
                            principalPaid = 0.0, // Calculé depuis API
                            interestRemaining = 0.0,
                            principalRemaining = dto.remaining ?: dto.principal,
                            totalDue = dto.principal + (dto.principal * (dto.interestRate / 100)),
                            balance = dto.remaining ?: dto.principal
                        )
                    }
                    _totalLoanAmount.value = result.data.sumOf { it.principal }
                    _totalRemainingAmount.value = result.data.sumOf { it.remaining ?: it.principal }
                    _isLoading.value = false
                }
                is ApiResult.Error -> {
                    Log.e("LoansViewModel", "Remote error: ${result.message}")
                    _isLoading.value = false
                    loadLoansFromLocal()
                }
            }
        }
    }
    
    private fun loadLoansFromLocal() {
        viewModelScope.launch {
            loanRepository.getAllLoans().collect { localLoans ->
                _loans.value = localLoans.map { loan ->
                    LoanDisplay(
                        id = loan.id,
                        principal = loan.principal,
                        interestRate = loan.interestRate,
                        termMonths = loan.termMonths,
                        startDate = loan.startDate,
                        lender = loan.lender,
                        purpose = loan.purpose,
                        totalRepaid = 0.0,
                        interestTotal = loan.principal * (loan.interestRate / 100),
                        interestPaid = 0.0,
                        principalPaid = 0.0,
                        interestRemaining = loan.principal * (loan.interestRate / 100),
                        principalRemaining = loan.principal,
                        totalDue = loan.principal + (loan.principal * (loan.interestRate / 100)),
                        balance = loan.principal
                    )
                }
                _totalLoanAmount.value = localLoans.sumOf { it.principal }
                _totalRemainingAmount.value = localLoans.sumOf { it.principal }
                _isLoading.value = false
            }
        }
    }

    fun calculateLoanBreakdown(loan: LoanDisplay): LoanBreakdown {
        return LoanBreakdown(
            totalInterest = loan.interestTotal,
            interestRemaining = loan.interestRemaining,
            principalRemaining = loan.principalRemaining,
            interestPaid = loan.interestPaid,
            principalPaid = loan.principalPaid,
            totalDue = loan.totalDue,
            progress = if (loan.totalDue > 0) ((loan.totalRepaid / loan.totalDue).toFloat().coerceIn(0f, 1f)) else 0f
        )
    }

    fun addLoan(loan: Loan) {
        viewModelScope.launch {
            if (_useRemote.value) {
                val dto = CreateLoanDto(
                    principal = loan.principal,
                    interestRate = loan.interestRate,
                    termMonths = loan.termMonths,
                    startDate = loan.startDate,
                    lender = loan.lender,
                    purpose = loan.purpose
                )
                val result = remoteRepository.createLoan(dto)
                if (result is ApiResult.Success) {
                    loadLoans()
                }
            } else {
                loanRepository.insertLoan(loan)
            }
        }
    }

    fun updateLoan(loan: Loan) {
        viewModelScope.launch {
            loanRepository.updateLoan(loan)
        }
    }

    fun deleteLoan(loan: LoanDisplay) {
        viewModelScope.launch {
            if (_useRemote.value) {
                val result = remoteRepository.deleteLoan(loan.id)
                if (result is ApiResult.Success) {
                    loadLoans()
                }
            } else {
                loanRepository.deleteLoan(Loan(
                    id = loan.id,
                    principal = loan.principal,
                    interestRate = loan.interestRate,
                    termMonths = loan.termMonths ?: 0,
                    startDate = loan.startDate ?: "",
                    lender = loan.lender,
                    purpose = loan.purpose
                ))
            }
        }
    }

    fun addRepayment(loan: LoanDisplay, interestAmount: Double, principalAmount: Double) {
        viewModelScope.launch {
            if (_useRemote.value) {
                val dto = CreateRepaymentDto(
                    loanId = loan.id,
                    amount = interestAmount + principalAmount,
                    interestAmount = interestAmount,
                    principalAmount = principalAmount,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                val result = remoteRepository.createRepayment(dto)
                if (result is ApiResult.Success) {
                    loadLoans()
                }
            } else {
                val repayment = Repayment(
                    loanId = loan.id,
                    amount = interestAmount + principalAmount,
                    interestAmount = interestAmount,
                    principalAmount = principalAmount,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                repaymentRepository.insertRepayment(repayment)
            }
        }
    }
    
    fun refresh() {
        loadLoans()
    }
}
