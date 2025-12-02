package com.mbongo.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoneyCard(
    balance: Double,
    hasSalary: Boolean,
    loansBalance: Double,
    currentMonth: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBalance by remember { mutableStateOf(false) }
    
    // Formater le mois
    val monthFormatted = remember(currentMonth) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val date = sdf.parse(currentMonth) ?: Date()
            val monthFormat = SimpleDateFormat("MM/yyyy", Locale.FRENCH)
            monthFormat.format(date)
        } catch (e: Exception) {
            currentMonth
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp),
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
                        text = "Solde disponible - $monthFormatted",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (showBalance) formatCurrency(balance) else "•••••••••",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (balance >= 0) MaterialTheme.colorScheme.primary else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Alertes
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!hasSalary) {
                            Text(
                                text = "⚠️ Pas de salaire ce mois",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFBBF24)
                            )
                        }
                        if (loansBalance > 0) {
                            Text(
                                text = "📋 Prêts: ${if (showBalance) formatCurrency(loansBalance) else "•••"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFF87171)
                            )
                        }
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    IconButton(
                        onClick = { showBalance = !showBalance },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = if (showBalance) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showBalance) "Masquer" else "Afficher",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

fun formatCurrency(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRENCH)
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return "${formatter.format(value)} FCFA"
}
