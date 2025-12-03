@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.mbongo.app.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.mbongo.app.ui.viewmodel.DashboardViewModel
import com.mbongo.app.ui.viewmodel.ManagementLevel
import com.mbongo.app.ui.components.CopyrightFooter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Mbongo",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gestion financière personnelle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Carte de Gestion (Management Card)
        item {
            ManagementCard(
                usagePercent = uiState.usagePercent,
                managementLevel = uiState.managementLevel,
                hasSalary = uiState.hasSalary,
                currentMonth = uiState.currentMonth,
                previousMonthIncome = uiState.previousMonthIncome,
                previousMonthExpenses = uiState.previousMonthExpenses,
                previousMonthBalance = uiState.previousMonthBalance
            )
        }
        
        // Balance Card (Money Card)
        item {
            MoneyCard(
                balance = uiState.balance,
                currency = "FCFA",
                month = uiState.currentMonth
            )
        }
        
        // Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Revenus",
                    amount = uiState.totalIncome,
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatCard(
                    title = "Dépenses",
                    amount = uiState.totalExpenses,
                    icon = Icons.Default.TrendingDown,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Épargne",
                    amount = uiState.totalSavings,
                    icon = Icons.Default.Savings,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondary
                )
                StatCard(
                    title = "Prêts",
                    amount = uiState.totalLoans,
                    icon = Icons.Default.AccountBalance,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Quick Actions
        item {
            Text(
                text = "Actions rapides",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionTile(
                    title = "Ajouter\nDépense",
                    icon = Icons.Default.Add,
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
                ActionTile(
                    title = "Ajouter\nRevenu",
                    icon = Icons.Default.AddCircle,
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
                ActionTile(
                    title = "Voir\nStats",
                    icon = Icons.Default.BarChart,
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Recent Transactions (placeholder)
        item {
            Text(
                text = "Transactions récentes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Aucune transaction",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun ManagementCard(
    usagePercent: Int,
    managementLevel: ManagementLevel,
    hasSalary: Boolean,
    currentMonth: String,
    previousMonthIncome: Double,
    previousMonthExpenses: Double,
    previousMonthBalance: Double,
    modifier: Modifier = Modifier
) {
    var showPreviousMonth by remember { mutableStateOf(false) }
    
    // Couleurs selon le niveau
    val (backgroundColor, iconColor, emoji, title, advice) = when (managementLevel) {
        ManagementLevel.GOOD -> {
            ManagementStyle(
                Color(0xFF10B981).copy(alpha = 0.15f),
                Color(0xFF10B981),
                "✨",
                "Excellente gestion",
                "Continuez ainsi ! Vous gérez bien votre budget."
            )
        }
        ManagementLevel.WARNING -> {
            ManagementStyle(
                Color(0xFFF59E0B).copy(alpha = 0.15f),
                Color(0xFFF59E0B),
                "⚠️",
                "Attention",
                "Vous approchez de votre limite. Réduisez les dépenses non essentielles."
            )
        }
        ManagementLevel.BAD -> {
            ManagementStyle(
                Color(0xFFEF4444).copy(alpha = 0.15f),
                Color(0xFFEF4444),
                "🚨",
                "Dépassement",
                "Vos dépenses dépassent vos revenus ! Revoyez votre budget immédiatement."
            )
        }
    }
    
    // Formater le mois précédent
    val previousMonthFormatted = remember(currentMonth) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = sdf.parse(currentMonth) ?: Date()
            calendar.add(Calendar.MONTH, -1)
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
            monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            "Mois précédent"
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // En-tête avec emoji et titre
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = iconColor,
                            fontWeight = FontWeight.Bold
                        )
                        if (!hasSalary) {
                            Text(
                                text = "Aucun salaire enregistré",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
                
                // Pourcentage
                Text(
                    text = "$usagePercent%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Barre de progression
            LinearProgressIndicator(
                progress = (usagePercent / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = iconColor,
                trackColor = iconColor.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Conseil
            Text(
                text = advice,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            // Section récapitulatif mois précédent (dépliable)
            if (previousMonthIncome > 0 || previousMonthExpenses > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider(color = iconColor.copy(alpha = 0.3f))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPreviousMonth = !showPreviousMonth }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 Récap $previousMonthFormatted",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (showPreviousMonth) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showPreviousMonth) "Réduire" else "Développer",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                AnimatedVisibility(
                    visible = showPreviousMonth,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PreviousMonthRow(
                            label = "Revenus",
                            amount = previousMonthIncome,
                            color = Color(0xFF10B981)
                        )
                        PreviousMonthRow(
                            label = "Dépenses",
                            amount = previousMonthExpenses,
                            color = Color(0xFFEF4444)
                        )
                        PreviousMonthRow(
                            label = "Solde",
                            amount = previousMonthBalance,
                            color = if (previousMonthBalance >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}

data class ManagementStyle(
    val backgroundColor: Color,
    val iconColor: Color,
    val emoji: String,
    val title: String,
    val advice: String
)

@Composable
fun PreviousMonthRow(
    label: String,
    amount: Double,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "${String.format("%,.0f", amount)} FCFA",
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MoneyCard(
    balance: Double,
    currency: String,
    month: String,
    modifier: Modifier = Modifier
) {
    // Formater le mois
    val monthFormatted = remember(month) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val date = sdf.parse(month) ?: Date()
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
            monthFormat.format(date).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            month
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SOLDE $monthFormatted".uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${String.format("%,.0f", balance)} $currency",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (balance >= 0) MaterialTheme.colorScheme.primary else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${String.format("%,.0f", amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
