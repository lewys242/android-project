package com.mbongo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Niveau de gestion
enum class ManagementLevel {
    GOOD,      // < 80% utilisé
    WARNING,   // 80-100% utilisé
    BAD        // > 100% utilisé
}

data class ManagementAdvice(
    val title: String,
    val emoji: String,
    val tips: List<String>
)

@Composable
fun ManagementCard(
    usagePercent: Int,
    managementLevel: ManagementLevel,
    totalIncome: Double,
    totalExpenses: Double,
    balance: Double,
    hasSalary: Boolean,
    previousMonthIncome: Double,
    previousMonthExpenses: Double,
    previousMonthBalance: Double,
    previousMonth: String,
    modifier: Modifier = Modifier
) {
    var showAdvice by remember { mutableStateOf(false) }
    var showPreviousMonth by remember { mutableStateOf(false) }
    
    // Couleurs et styles selon le niveau
    val (backgroundColor, borderColor, emoji, title) = when (managementLevel) {
        ManagementLevel.GOOD -> listOf(
            Color(0xFF10B981).copy(alpha = 0.1f),
            Color(0xFF10B981),
            "✅",
            "Bonne gestion"
        )
        ManagementLevel.WARNING -> listOf(
            Color(0xFFF59E0B).copy(alpha = 0.1f),
            Color(0xFFF59E0B),
            "⚠️",
            "Attention"
        )
        ManagementLevel.BAD -> listOf(
            Color(0xFFEF4444).copy(alpha = 0.1f),
            Color(0xFFEF4444),
            "❌",
            "Mauvaise gestion"
        )
    }
    
    val advice = when (managementLevel) {
        ManagementLevel.GOOD -> ManagementAdvice(
            title = "🎉 Excellent ! Vous gérez bien votre budget",
            emoji = "✅",
            tips = listOf(
                "Continuez ainsi ! Pensez à épargner le surplus",
                "Profitez-en pour constituer un fond d'urgence",
                "Vous pouvez vous faire un petit plaisir raisonnable 🎁"
            )
        )
        ManagementLevel.WARNING -> ManagementAdvice(
            title = "⚡ Vous approchez de votre limite",
            emoji = "⚠️",
            tips = listOf(
                "Surveillez vos postes de dépenses cette fin de mois",
                "Évitez les achats impulsifs",
                "Gardez une marge pour les imprévus"
            )
        )
        ManagementLevel.BAD -> ManagementAdvice(
            title = "🚨 Vous dépensez plus que vous gagnez !",
            emoji = "❌",
            tips = listOf(
                "Identifiez les dépenses non essentielles à réduire",
                "Reportez les achats non urgents au mois prochain",
                "Cherchez des sources de revenus complémentaires"
            )
        )
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor as Color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // En-tête
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
                        text = emoji as String,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            text = title as String,
                            style = MaterialTheme.typography.titleMedium,
                            color = borderColor as Color,
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
                    color = borderColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Récapitulatif
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "💰 Salaire:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatCurrency(totalIncome),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📉 Dépensé:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatCurrency(totalExpenses),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFEF4444)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📊 Reste:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatCurrency(balance),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (balance >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Boutons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAdvice = !showAdvice },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = borderColor
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (showAdvice) "▼ Conseils" else "▶ Conseils",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (previousMonthIncome > 0) {
                    Button(
                        onClick = { showPreviousMonth = !showPreviousMonth },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (showPreviousMonth) "▼ $previousMonth" else "▶ $previousMonth",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Conseils (dépliable)
            if (showAdvice) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = advice.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = borderColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        advice.tips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            
            // Récap mois précédent (dépliable)
            if (showPreviousMonth && previousMonthIncome > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "📅 Récap $previousMonth",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("💰 Salaire:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = formatCurrency(previousMonthIncome),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF10B981)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📉 Dépensé:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = formatCurrency(previousMonthExpenses),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFEF4444)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📊 Solde:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = formatCurrency(previousMonthBalance),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (previousMonthBalance >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                        if (previousMonthIncome > 0) {
                            val prevPercent = ((previousMonthExpenses / previousMonthIncome) * 100).toInt()
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Taux: $prevPercent%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
