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
    
    // Rafraîchir les données à chaque fois que l'écran devient visible
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
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
        
        // Carte de Gestion (Management Card) - comme web
        item {
            ManagementCard(
                usagePercent = uiState.usagePercent,
                managementLevel = uiState.managementLevel,
                hasSalary = uiState.hasSalary,
                currentMonth = uiState.currentMonth,
                previousMonthIncome = uiState.previousMonthIncome,
                previousMonthExpenses = uiState.previousMonthExpenses,
                previousMonthBalance = uiState.previousMonthBalance,
                totalIncome = uiState.totalIncome,
                totalExpenses = uiState.totalExpenses,
                balance = uiState.balance
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
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Carte Épargne avec options 5% et 10%
        item {
            SavingsCardDashboard(
                totalIncome = uiState.totalIncome,
                savingsRate = uiState.savingsRate,
                savingsEnabled = uiState.savingsEnabled,
                onRateChange = { rate -> viewModel.setSavingsRate(rate) },
                onEnabledChange = { enabled -> viewModel.setSavingsEnabled(enabled) }
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Carte Prêts
                StatCard(
                    title = "Prêts",
                    amount = uiState.totalLoans,
                    icon = Icons.Default.AccountBalance,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                // Carte Transactions (comme web)
                TransactionCountCard(
                    count = uiState.transactionCount,
                    modifier = Modifier.weight(1f)
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
    totalIncome: Double = 0.0,
    totalExpenses: Double = 0.0,
    balance: Double = 0.0,
    modifier: Modifier = Modifier
) {
    var showPreviousMonth by remember { mutableStateOf(false) }
    var showAdvice by remember { mutableStateOf(false) }
    
    // Couleurs selon le niveau (comme dans le web)
    val (backgroundColor, iconColor, emoji, title, advice) = when (managementLevel) {
        ManagementLevel.GOOD -> {
            ManagementStyle(
                Color(0xFF1A2A1A), // Fond vert foncé comme web
                Color(0xFF10B981),
                "✅",
                "Bonne gestion",
                "Continuez ainsi ! Vous gérez bien votre budget."
            )
        }
        ManagementLevel.WARNING -> {
            ManagementStyle(
                Color(0xFF2A2A1A), // Fond jaune foncé comme web
                Color(0xFFF59E0B),
                "⚠️",
                "Attention",
                "Vous approchez de votre limite. Surveillez vos postes de dépenses."
            )
        }
        ManagementLevel.BAD -> {
            ManagementStyle(
                Color(0xFF2A2A2A), // Fond rouge foncé comme web
                Color(0xFFEF4444),
                "❌",
                "Mauvaise gestion",
                "Vos dépenses dépassent vos revenus ! Revoyez votre budget."
            )
        }
    }
    
    // Conseils selon le niveau (identique au web)
    val adviceList = when (managementLevel) {
        ManagementLevel.BAD -> listOf(
            "Identifiez les dépenses non essentielles à réduire",
            "Reportez les achats non urgents au mois prochain",
            "Cherchez des sources de revenus complémentaires"
        )
        ManagementLevel.WARNING -> listOf(
            "Surveillez vos postes de dépenses cette fin de mois",
            "Évitez les achats impulsifs",
            "Gardez une marge pour les imprévus"
        )
        ManagementLevel.GOOD -> listOf(
            "Continuez ainsi ! Pensez à épargner le surplus",
            "Profitez-en pour constituer un fond d'urgence",
            "Vous pouvez vous faire un petit plaisir raisonnable 🎁"
        )
    }
    
    // Titre conseil selon niveau
    val adviceTitle = when (managementLevel) {
        ManagementLevel.BAD -> "🚨 Vous dépensez plus que vous gagnez !"
        ManagementLevel.WARNING -> "⚡ Vous approchez de votre limite"
        ManagementLevel.GOOD -> "🎉 Excellent ! Vous gérez bien votre budget"
    }
    
    // Formater le mois précédent
    val previousMonthFormatted = remember(currentMonth) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = sdf.parse(currentMonth) ?: Date()
            calendar.add(Calendar.MONTH, -1)
            "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        } catch (e: Exception) {
            "?"
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, iconColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // En-tête avec emoji et titre (comme web)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Icône dans un cercle (comme web)
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = when (managementLevel) {
                            ManagementLevel.GOOD -> Color(0xFFD1FAE5)
                            ManagementLevel.WARNING -> Color(0xFFFEF3C7)
                            ManagementLevel.BAD -> Color(0xFFFEF2F2)
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = emoji,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$usagePercent%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        if (!hasSalary) {
                            Text(
                                text = "Aucun salaire enregistré",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Informations financières (comme web)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "💰 Salaire:", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${String.format("%,.0f", totalIncome)} FCFA",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "📉 Dépensé:", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${String.format("%,.0f", totalExpenses)} FCFA",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD4AF37)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "📊 Reste:", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${String.format("%,.0f", balance)} FCFA",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (balance >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Boutons d'action (comme web)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bouton conseils
                Button(
                    onClick = { showAdvice = !showAdvice },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = iconColor
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = if (showAdvice) "▼ Conseils" else "▶ Conseils",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
                
                // Bouton récap mois précédent
                if (previousMonthIncome > 0) {
                    Button(
                        onClick = { showPreviousMonth = !showPreviousMonth },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (showPreviousMonth) "▼ $previousMonthFormatted" else "▶ $previousMonthFormatted",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Section conseils dépliable (comme web)
            AnimatedVisibility(
                visible = showAdvice,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = adviceTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = iconColor,
                            fontWeight = FontWeight.Bold
                        )
                        adviceList.forEach { tip ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(text = "•", color = Color.White)
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Section récapitulatif mois précédent (dépliable)
            AnimatedVisibility(
                visible = showPreviousMonth && previousMonthIncome > 0,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "📅 Récap $previousMonthFormatted",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PreviousMonthRow(
                            label = "💰 Salaire",
                            amount = previousMonthIncome,
                            color = Color(0xFF10B981)
                        )
                        PreviousMonthRow(
                            label = "📉 Dépensé",
                            amount = previousMonthExpenses,
                            color = Color(0xFFD4AF37)
                        )
                        PreviousMonthRow(
                            label = "📊 Solde",
                            amount = previousMonthBalance,
                            color = if (previousMonthBalance >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                        if (previousMonthIncome > 0) {
                            val prevUsagePercent = ((previousMonthExpenses / previousMonthIncome) * 100).toInt()
                            Text(
                                text = "Taux: $prevUsagePercent%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
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
                        color = if (balance >= 0) MaterialTheme.colorScheme.primary else Color(0xFFF59E0B),
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

@Composable
fun SavingsCardDashboard(
    totalIncome: Double,
    savingsRate: Int,
    savingsEnabled: Boolean,
    onRateChange: (Int) -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val goldColor = Color(0xFFD4AF37)
    val darkBackground = Color(0xFF2A2A2A)
    val mediumGray = Color(0xFF3A3A3A)
    
    val savingsAmount = if (totalIncome > 0 && savingsEnabled) {
        totalIncome * (savingsRate / 100.0)
    } else 0.0
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💰",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Épargne recommandée",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = goldColor
                    )
                }
                
                // Toggle épargne
                Switch(
                    checked = savingsEnabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = goldColor,
                        checkedTrackColor = goldColor.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Montant de l'épargne
            Text(
                text = "${String.format("%,.0f", savingsAmount)} FCFA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (savingsEnabled) goldColor else Color.Gray
            )
            
            Text(
                text = if (savingsEnabled) "Basé sur $savingsRate% de vos revenus" else "Épargne désactivée",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Boutons de taux 5% et 10%
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bouton 5%
                Button(
                    onClick = { onRateChange(5) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (savingsRate == 5) goldColor else mediumGray,
                        contentColor = if (savingsRate == 5) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = savingsEnabled
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "5%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${String.format("%,.0f", totalIncome * 0.05)} F",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                // Bouton 10%
                Button(
                    onClick = { onRateChange(10) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (savingsRate == 10) goldColor else mediumGray,
                        contentColor = if (savingsRate == 10) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = savingsEnabled
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "10%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${String.format("%,.0f", totalIncome * 0.10)} F",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            if (totalIncome == 0.0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Ajoutez un revenu pour calculer l'épargne",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF59E0B)
                )
            }
        }
    }
}

@Composable
fun TransactionCountCard(
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icône (comme web)
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDBEAFE) // Bleu clair comme web
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6), // Bleu comme web
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
