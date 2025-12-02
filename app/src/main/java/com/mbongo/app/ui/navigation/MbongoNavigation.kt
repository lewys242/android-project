package com.mbongo.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mbongo.app.ui.screens.dashboard.DashboardScreen
import com.mbongo.app.ui.screens.expenses.ExpensesScreen
import com.mbongo.app.ui.screens.incomes.IncomesScreen
import com.mbongo.app.ui.screens.loans.LoansScreen
import com.mbongo.app.ui.screens.statistics.StatisticsScreen

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MbongoNavigation() {
    val navController = rememberNavController()
    
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Dashboard.route, "Accueil", Icons.Default.Home),
        BottomNavItem(Screen.Expenses.route, "Dépenses", Icons.Default.ShoppingCart),
        BottomNavItem(Screen.Incomes.route, "Revenus", Icons.Default.AttachMoney),
        BottomNavItem(Screen.Loans.route, "Prêts", Icons.Default.AccountBalance),
        BottomNavItem(Screen.Statistics.route, "Stats", Icons.Default.BarChart)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController)
            }
            composable(Screen.Expenses.route) {
                ExpensesScreen(navController)
            }
            composable(Screen.Incomes.route) {
                IncomesScreen(navController)
            }
            composable(Screen.Budgets.route) {
                PlaceholderScreen("Budgets")
            }
            composable(Screen.Loans.route) {
                LoansScreen(navController)
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(navController)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Surface(
        modifier = Modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = "Écran $title - À implémenter",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
