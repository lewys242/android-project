package com.mbongo.app.data.repository

import android.util.Log
import com.mbongo.app.data.remote.ApiClient
import com.mbongo.app.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

@Singleton
class RemoteRepository @Inject constructor() {
    
    private val api = ApiClient.apiService
    
    // ============ CATEGORIES ============
    fun getCategories(): Flow<ApiResult<List<CategoryDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = api.getCategories()
            if (response.isSuccessful) {
                emit(ApiResult.Success(response.body() ?: emptyList()))
            } else {
                emit(ApiResult.Error("Erreur: ${response.message()}", response.code()))
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "getCategories error", e)
            emit(ApiResult.Error("Erreur réseau: ${e.localizedMessage}"))
        }
    }
    
    // ============ EXPENSES ============
    fun getExpenses(month: Int? = null, year: Int? = null, categoryId: Long? = null): Flow<ApiResult<List<ExpenseDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = api.getExpenses(month, year, categoryId)
            if (response.isSuccessful) {
                emit(ApiResult.Success(response.body() ?: emptyList()))
            } else {
                emit(ApiResult.Error("Erreur: ${response.message()}", response.code()))
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "getExpenses error", e)
            emit(ApiResult.Error("Erreur réseau: ${e.localizedMessage}"))
        }
    }
    
    suspend fun createExpense(expense: CreateExpenseDto): ApiResult<ExpenseDto> {
        return try {
            val response = api.createExpense(expense)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Erreur: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "createExpense error", e)
            ApiResult.Error("Erreur réseau: ${e.localizedMessage}")
        }
    }
    
    suspend fun deleteExpense(id: Long): ApiResult<Unit> {
        return try {
            val response = api.deleteExpense(id)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Erreur: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "deleteExpense error", e)
            ApiResult.Error("Erreur réseau: ${e.localizedMessage}")
        }
    }
    
    // ============ INCOMES ============
    fun getIncomes(month: String? = null): Flow<ApiResult<List<IncomeDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = api.getIncomes(month)
            if (response.isSuccessful) {
                emit(ApiResult.Success(response.body() ?: emptyList()))
            } else {
                emit(ApiResult.Error("Erreur: ${response.message()}", response.code()))
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "getIncomes error", e)
            emit(ApiResult.Error("Erreur réseau: ${e.localizedMessage}"))
        }
    }
    
    suspend fun createIncome(income: CreateIncomeDto): ApiResult<IncomeDto> {
        return try {
            val response = api.createIncome(income)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Erreur: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "createIncome error", e)
            ApiResult.Error("Erreur réseau: ${e.localizedMessage}")
        }
    }
    
    suspend fun deleteIncome(id: Long): ApiResult<Unit> {
        return try {
            val response = api.deleteIncome(id)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Erreur: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "deleteIncome error", e)
            ApiResult.Error("Erreur réseau: ${e.localizedMessage}")
        }
    }
    
    // ============ LOANS ============
    fun getLoans(): Flow<ApiResult<List<LoanDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = api.getLoans()
            if (response.isSuccessful) {
                emit(ApiResult.Success(response.body() ?: emptyList()))
            } else {
                emit(ApiResult.Error("Erreur: ${response.message()}", response.code()))
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "getLoans error", e)
            emit(ApiResult.Error("Erreur réseau: ${e.localizedMessage}"))
        }
    }
    
    suspend fun createLoan(loan: CreateLoanDto): ApiResult<LoanDto> {
        return try {
            val response = api.createLoan(loan)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Erreur: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "createLoan error", e)
            ApiResult.Error("Erreur réseau: ${e.localizedMessage}")
        }
    }
    
    suspend fun deleteLoan(id: Long): ApiResult<Unit> {
        return try {
            val response = api.deleteLoan(id)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Erreur: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "deleteLoan error", e)
            ApiResult.Error("Erreur réseau: ${e.localizedMessage}")
        }
    }
    
    // ============ REPAYMENTS ============
    fun getRepayments(loanId: Long? = null): Flow<ApiResult<List<RepaymentDto>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = api.getRepayments(loanId)
            if (response.isSuccessful) {
                emit(ApiResult.Success(response.body() ?: emptyList()))
            } else {
                emit(ApiResult.Error("Erreur: ${response.message()}", response.code()))
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "getRepayments error", e)
            emit(ApiResult.Error("Erreur réseau: ${e.localizedMessage}"))
        }
    }
    
    suspend fun createRepayment(repayment: CreateRepaymentDto): ApiResult<RepaymentDto> {
        return try {
            val response = api.createRepayment(repayment)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Erreur: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "createRepayment error", e)
            ApiResult.Error("Erreur réseau: ${e.localizedMessage}")
        }
    }
    
    // ============ STATS ============
    fun getStats(month: Int? = null, year: Int? = null): Flow<ApiResult<StatsDto>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = api.getStats(month, year)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResult.Success(response.body()!!))
            } else {
                emit(ApiResult.Error("Erreur: ${response.message()}", response.code()))
            }
        } catch (e: Exception) {
            Log.e("RemoteRepository", "getStats error", e)
            emit(ApiResult.Error("Erreur réseau: ${e.localizedMessage}"))
        }
    }
}
