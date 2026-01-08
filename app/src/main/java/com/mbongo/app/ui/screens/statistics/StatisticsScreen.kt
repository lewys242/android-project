package com.mbongo.app.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mbongo.app.ui.components.*
import com.mbongo.app.ui.viewmodel.StatisticsViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.statisticsState.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val currentYear by viewModel.currentYear.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    
    // Extraire mois et année du format "yyyy-MM"
    val monthNum = currentMonth.split("-").getOrNull(1)?.toIntOrNull() ?: 1
    val yearNum = currentYear.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
    
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 0
        currency = Currency.getInstance("XAF")
    }}
    
    fun formatCurrency(amount: Double): String {
        return "${String.format("%,.0f", amount).replace(",", " ")} FCFA"
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "📊 Statistiques",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Sélecteur de période
        item {
            MonthYearSelector(
                selectedMonth = monthNum,
                selectedYear = yearNum,
                viewMode = viewMode,
                onMonthYearChange = { m, y -> viewModel.setMonth(m, y) },
                onViewModeChange = { viewModel.setViewMode(it) }
            )
        }

        // Carte résumé principal - Thème sombre
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (viewMode == "month") "Résumé du mois" else "Résumé de l'année",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFD4AF37),
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFD4AF37))
                        }
                    } else {
                        // Revenus
                        StatRow(
                            label = "Revenus",
                            value = formatCurrency(state.totalIncome),
                            color = Color(0xFF10B981)
                        )
                        
                        // Dépenses
                        StatRow(
                            label = "Dépenses",
                            value = formatCurrency(state.totalExpenses),
                            color = Color(0xFFF59E0B)
                        )
                        
                        Divider(color = Color(0xFF3A3A3A))
                        
                        // Balance
                        StatRow(
                            label = "Balance",
                            value = formatCurrency(state.balance),
                            color = if (state.balance >= 0) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                        
                        // Barre de progression
                        if (state.totalIncome > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Utilisation du budget: ${state.usedPercentage.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCCCCCC)
                            )
                            LinearProgressIndicator(
                                progress = state.usedPercentage / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = when {
                                    state.usedPercentage > 90 -> Color(0xFFF59E0B)
                                    state.usedPercentage > 70 -> Color(0xFFD4AF37)
                                    else -> Color(0xFF10B981)
                                },
                                trackColor = Color(0xFF3A3A3A),
                            )
                        }
                    }
                }
            }
        }

        // Graphique Revenus vs Dépenses
        if (!state.isLoading && state.totalIncome > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💰 Revenus vs Dépenses",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        IncomeExpensePieChart(
                            income = state.totalIncome,
                            expenses = state.totalExpenses
                        )
                    }
                }
            }
        }

        // Graphique par catégorie
        if (!state.isLoading && state.categoryStats.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📈 Dépenses par catégorie",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val pieEntries = state.categoryStats.map { cs ->
                            PieChartEntry(
                                label = cs.category.name,
                                value = cs.total.toFloat(),
                                color = try {
                                    Color(android.graphics.Color.parseColor(cs.category.color))
                                } catch (e: Exception) {
                                    Color(0xFFD4AF37)
                                },
                                icon = cs.category.icon
                            )
                        }
                        
                        PieChart(
                            entries = pieEntries,
                            centerText = "${state.categoryStats.size}\ncatégories"
                        )
                    }
                }
            }
            
            // Liste détaillée des catégories
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📋 Détail par catégorie",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold
                        )
                        
                        state.categoryStats.forEach { cs ->
                            CategoryStatItem(
                                icon = cs.category.icon,
                                name = cs.category.name,
                                amount = formatCurrency(cs.total),
                                percentage = cs.percentage,
                                color = try {
                                    Color(android.graphics.Color.parseColor(cs.category.color))
                                } catch (e: Exception) {
                                    Color(0xFFD4AF37)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Évolution mensuelle (visible seulement en mode année)
        if (!state.isLoading && viewMode == "year") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "📅 Évolution mensuelle $currentYear",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        MonthlyBarChart(
                            data = state.monthlyTotals
                        )
                    }
                }
            }
        }
        
        // Message si pas de données
        if (!state.isLoading && state.totalIncome == 0.0 && state.totalExpenses == 0.0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "Aucune donnée",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Commencez par ajouter des revenus et des dépenses pour voir vos statistiques.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFCCCCCC),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
        
        // Copyright Footer
        item {
            CopyrightFooter()
        }
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFCCCCCC)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryStatItem(
    icon: String,
    name: String,
    amount: String,
    percentage: Float,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${percentage.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCCCCCC)
                )
            }
        }
        
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF3A3A3A),
        )
    }
}
