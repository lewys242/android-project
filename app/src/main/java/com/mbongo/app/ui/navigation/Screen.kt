package com.mbongo.app.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Expenses : Screen("expenses")
    object Incomes : Screen("incomes")
    object Budgets : Screen("budgets")
    object Loans : Screen("loans")
    object Statistics : Screen("statistics")
    object Categories : Screen("categories")
    object Settings : Screen("settings")
}
