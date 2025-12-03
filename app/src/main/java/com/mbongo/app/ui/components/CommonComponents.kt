package com.mbongo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// Couleurs communes
object AppColors {
    val background = Color(0xFF1A1A1A)
    val cardBackground = Color(0xFF2A2A2A)
    val cardBorder = Color(0xFFD4AF37)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFCCCCCC)
    val textMuted = Color(0xFF9CA3AF)
    val gold = Color(0xFFD4AF37)
    val green = Color(0xFF10B981)
    val red = Color(0xFFEF4444)
    val yellow = Color(0xFFFBBF24)
    val blue = Color(0xFF667EEA)
    
    // Pour les formulaires sur fond blanc
    val inputBackground = Color(0xFFFFFFFF)
    val inputText = Color(0xFF1E293B)
    val inputBorder = Color(0xFFCBD5E1)
    val inputBorderFocused = Color(0xFF10B981)
}

// TextField avec style compatible thème sombre
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MbongoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = AppColors.textSecondary) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        textStyle = TextStyle(
            color = AppColors.textPrimary,
            fontSize = 16.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AppColors.textPrimary,
            unfocusedTextColor = AppColors.textPrimary,
            cursorColor = AppColors.gold,
            focusedBorderColor = AppColors.gold,
            unfocusedBorderColor = AppColors.cardBorder.copy(alpha = 0.5f),
            focusedLabelColor = AppColors.gold,
            unfocusedLabelColor = AppColors.textMuted,
            focusedLeadingIconColor = AppColors.gold,
            unfocusedLeadingIconColor = AppColors.textMuted,
            focusedTrailingIconColor = AppColors.textMuted,
            unfocusedTrailingIconColor = AppColors.textMuted,
            errorBorderColor = AppColors.red,
            errorLabelColor = AppColors.red,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

// Card avec style Mbongo (fond sombre, bordure dorée)
@Composable
fun MbongoCard(
    modifier: Modifier = Modifier,
    hasBorder: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (hasBorder) Modifier.border(
                    width = 1.dp,
                    color = AppColors.cardBorder,
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.cardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// Money Card identique à l'app web
@Composable
fun WebStyleMoneyCard(
    availableBalance: Double,
    hasSalary: Boolean,
    loansBalance: Double,
    showBalance: Boolean,
    onToggleBalance: () -> Unit,
    onRefresh: () -> Unit,
    currentMonth: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF667EEA) // Violet/bleu comme l'app web
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Solde disponible - $currentMonth",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (showBalance) formatCurrency(availableBalance) else "•••••••••",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Alertes
                    if (!hasSalary) {
                        Text(
                            text = "⚠️ Pas de salaire ce mois",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFBBF24)
                        )
                    }
                    if (loansBalance > 0) {
                        Text(
                            text = "📋 Prêts en cours: ${if (showBalance) formatCurrency(loansBalance) else "•••"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF87171)
                        )
                    }
                }
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = onToggleBalance,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (showBalance) 
                                Icons.Default.Visibility
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (showBalance) "Masquer" else "Afficher",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Bouton style app web
@Composable
fun MbongoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    isDanger: Boolean = false,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    val backgroundColor = when {
        isDanger -> AppColors.red
        isPrimary -> AppColors.green
        else -> AppColors.cardBackground
    }
    
    val contentColor = if (isPrimary || isDanger) Color.White else AppColors.textPrimary
    
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Section dépliable comme l'app web
@Composable
fun CollapsibleSection(
    title: String,
    emoji: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    MbongoCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$emoji $title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
            Text(
                text = if (isExpanded) "▼" else "▶",
                color = AppColors.gold
            )
        }
        
        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
