package com.mbongo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SavingsCard(
    totalIncome: Double,
    savingsRate: Int,
    savingsEnabled: Boolean,
    onRateChange: (Int) -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onSaveNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savingsAmount = if (totalIncome > 0) {
        (totalIncome * (savingsRate / 100.0))
    } else 0.0
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "💰 Épargne recommandée",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Montant de l'épargne
            Text(
                text = formatCurrency(savingsAmount),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
            
            Text(
                text = "Basé sur $savingsRate% du salaire",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Contrôles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Boutons de taux
                Button(
                    onClick = { onRateChange(10) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (savingsRate == 10) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "10%",
                        color = if (savingsRate == 10) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = { onRateChange(5) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (savingsRate == 5) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "5%",
                        color = if (savingsRate == 5) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Toggle épargne
                Switch(
                    checked = savingsEnabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF10B981),
                        checkedTrackColor = Color(0xFF10B981).copy(alpha = 0.5f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bouton enregistrer
            Button(
                onClick = onSaveNow,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                ),
                enabled = totalIncome > 0 && savingsAmount > 0
            ) {
                Text("Enregistrer maintenant")
            }
        }
    }
}
