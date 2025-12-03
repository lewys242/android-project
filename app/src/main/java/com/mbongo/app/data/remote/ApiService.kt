package com.mbongo.app.data.remote

import com.mbongo.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ============ CATEGORIES ============
    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
    
    // ============ EXPENSES ============
    @GET("expenses")
    suspend fun getExpenses(
        @Query("month") month: Int? = null,
        @Query("year") year: Int? = null,
        @Query("category_id") categoryId: Long? = null
    ): Response<List<ExpenseDto>>
    
    @POST("expenses")
    suspend fun createExpense(@Body expense: CreateExpenseDto): Response<ExpenseDto>
    
    @PUT("expenses/{id}")
    suspend fun updateExpense(@Path("id") id: Long, @Body expense: CreateExpenseDto): Response<ExpenseDto>
    
    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long): Response<Unit>
    
    // ============ INCOMES ============
    @GET("incomes")
    suspend fun getIncomes(
        @Query("month") month: String? = null
    ): Response<List<IncomeDto>>
    
    @POST("incomes")
    suspend fun createIncome(@Body income: CreateIncomeDto): Response<IncomeDto>
    
    @PUT("incomes/{id}")
    suspend fun updateIncome(@Path("id") id: Long, @Body income: CreateIncomeDto): Response<IncomeDto>
    
    @DELETE("incomes/{id}")
    suspend fun deleteIncome(@Path("id") id: Long): Response<Unit>
    
    // ============ LOANS ============
    @GET("loans")
    suspend fun getLoans(): Response<List<LoanDto>>
    
    @POST("loans")
    suspend fun createLoan(@Body loan: CreateLoanDto): Response<LoanDto>
    
    @DELETE("loans/{id}")
    suspend fun deleteLoan(@Path("id") id: Long): Response<Unit>
    
    // ============ REPAYMENTS ============
    @GET("repayments")
    suspend fun getRepayments(@Query("loan_id") loanId: Long? = null): Response<List<RepaymentDto>>
    
    @POST("repayments")
    suspend fun createRepayment(@Body repayment: CreateRepaymentDto): Response<RepaymentDto>
    
    // ============ STATS ============
    @GET("stats")
    suspend fun getStats(
        @Query("month") month: Int? = null,
        @Query("year") year: Int? = null
    ): Response<StatsDto>
    
    // ============ BUDGETS ============
    @GET("budgets")
    suspend fun getBudgets(@Query("month") month: String? = null): Response<List<BudgetDto>>
    
    @POST("budgets")
    suspend fun createOrUpdateBudget(@Body budget: CreateBudgetDto): Response<BudgetDto>
}
