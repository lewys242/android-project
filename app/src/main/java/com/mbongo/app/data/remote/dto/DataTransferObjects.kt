package com.mbongo.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// ============ CATEGORY ============
data class CategoryDto(
    val id: Long,
    val name: String,
    val color: String?,
    val icon: String?,
    val type: String? // "expense" ou "income"
)

// ============ EXPENSE ============
data class ExpenseDto(
    val id: Long,
    val amount: Double,
    val description: String?,
    val date: String,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("category_name") val categoryName: String?,
    val color: String?,
    val icon: String?
)

data class CreateExpenseDto(
    val amount: Double,
    val description: String?,
    val date: String,
    @SerializedName("category_id") val categoryId: Long
)

// ============ INCOME ============
data class IncomeDto(
    val id: Long,
    val amount: Double,
    val description: String?,
    val month: String,
    val date: String?,
    val type: String?, // "salary" ou "other"
    @SerializedName("category_id") val categoryId: Long?
)

data class CreateIncomeDto(
    val amount: Double,
    val description: String?,
    val month: String,
    val date: String?,
    val type: String?,
    @SerializedName("category_id") val categoryId: Long?
)

// ============ LOAN ============
data class LoanDto(
    val id: Long,
    val principal: Double,
    @SerializedName("interest_rate") val interestRate: Double,
    @SerializedName("term_months") val termMonths: Int,
    @SerializedName("start_date") val startDate: String,
    val lender: String?,
    val purpose: String?,
    // Champs calculés
    @SerializedName("total_repaid") val totalRepaid: Double?,
    @SerializedName("remaining") val remaining: Double?
)

data class CreateLoanDto(
    val principal: Double,
    @SerializedName("interest_rate") val interestRate: Double,
    @SerializedName("term_months") val termMonths: Int,
    @SerializedName("start_date") val startDate: String,
    val lender: String?,
    val purpose: String?
)

// ============ REPAYMENT ============
data class RepaymentDto(
    val id: Long,
    @SerializedName("loan_id") val loanId: Long,
    val amount: Double,
    @SerializedName("interest_amount") val interestAmount: Double?,
    @SerializedName("principal_amount") val principalAmount: Double?,
    val date: String
)

data class CreateRepaymentDto(
    @SerializedName("loan_id") val loanId: Long,
    val amount: Double,
    @SerializedName("interest_amount") val interestAmount: Double?,
    @SerializedName("principal_amount") val principalAmount: Double?,
    val date: String
)

// ============ STATS ============
data class StatsDto(
    @SerializedName("total_income") val totalIncome: Double,
    @SerializedName("total_expenses") val totalExpenses: Double,
    val balance: Double,
    @SerializedName("expenses_by_category") val expensesByCategory: List<CategoryStatsDto>?
)

data class CategoryStatsDto(
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("category_name") val categoryName: String,
    val total: Double,
    val color: String?,
    val icon: String?
)

// ============ BUDGET ============
data class BudgetDto(
    val id: Long,
    @SerializedName("category_id") val categoryId: Long,
    val month: String,
    val amount: Double,
    @SerializedName("category_name") val categoryName: String?
)

data class CreateBudgetDto(
    @SerializedName("category_id") val categoryId: Long,
    val month: String,
    val amount: Double
)
