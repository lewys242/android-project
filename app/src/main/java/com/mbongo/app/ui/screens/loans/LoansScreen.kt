@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.mbongo.app.ui.screens.loans

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mbongo.app.data.local.entity.Loan
import com.mbongo.app.data.local.entity.Repayment
import com.mbongo.app.ui.viewmodel.LoansViewModel
import com.mbongo.app.ui.components.CopyrightFooter
import com.mbongo.app.ui.components.formatCurrency
import java.text.SimpleDateFormat
import java.util.*

data class LoanBreakdown(
    val totalInterest: Double,
    val interestRemaining: Double,
    val principalRemaining: Double,
    val interestPaid: Double,
    val principalPaid: Double,
    val totalDue: Double,
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
    navController: NavController,
    viewModel: LoansViewModel = hiltViewModel()
) {
    val loans by viewModel.loans.collectAsState()
    val totalLoanAmount by viewModel.totalLoanAmount.collectAsState()
    val totalRemainingAmount by viewModel.totalRemainingAmount.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showRepaymentDialog by remember { mutableStateOf<Loan?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter prêt")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Prêts",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Total prêté",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrency(totalLoanAmount),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Restant",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrency(totalRemainingAmount),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loans List - Filtrer les prêts actifs
            val activeLoans = loans.filter { loan ->
                val breakdown = calculateLoanBreakdown(loan)
                (breakdown.interestRemaining + breakdown.principalRemaining) > 0
            }
            
            val paidLoans = loans.filter { loan ->
                val breakdown = calculateLoanBreakdown(loan)
                (breakdown.interestRemaining + breakdown.principalRemaining) <= 0
            }

            if (activeLoans.isEmpty() && paidLoans.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Aucun prêt",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Appuyez sur + pour ajouter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                CopyrightFooter()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Prêts actifs
                    if (activeLoans.isNotEmpty()) {
                        item {
                            Text(
                                text = "Prêts en cours (${activeLoans.size})",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        items(activeLoans) { loan ->
                            LoanItemEnhanced(
                                loan = loan,
                                breakdown = calculateLoanBreakdown(loan),
                                onDelete = { viewModel.deleteLoan(loan) },
                                onRepay = { showRepaymentDialog = loan }
                            )
                        }
                    }
                    
                    // Prêts soldés
                    if (paidLoans.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Prêts soldés (${paidLoans.size})",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        items(paidLoans) { loan ->
                            LoanItemEnhanced(
                                loan = loan,
                                breakdown = calculateLoanBreakdown(loan),
                                onDelete = { viewModel.deleteLoan(loan) },
                                onRepay = null,
                                isPaid = true
                            )
                        }
                    }
                    
                    item {
                        CopyrightFooter()
                    }
                }
            }
        }

        if (showAddDialog) {
            AddLoanDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { loan ->
                    viewModel.addLoan(loan)
                    showAddDialog = false
                }
            )
        }
        
        showRepaymentDialog?.let { loan ->
            RepaymentDialog(
                loan = loan,
                breakdown = calculateLoanBreakdown(loan),
                onDismiss = { showRepaymentDialog = null },
                onConfirm = { interestAmount, principalAmount ->
                    viewModel.addRepayment(
                        loan = loan,
                        interestAmount = interestAmount,
                        principalAmount = principalAmount
                    )
                    showRepaymentDialog = null
                }
            )
        }
    }
}

fun calculateLoanBreakdown(loan: Loan): LoanBreakdown {
    val principal = loan.principal
    val interestRate = loan.interestRate
    val totalInterest = principal * (interestRate / 100)
    val totalDue = principal + totalInterest
    
    // TODO: Récupérer les vrais remboursements depuis la DB
    val interestPaid = 0.0
    val principalPaid = 0.0
    
    val interestRemaining = (totalInterest - interestPaid).coerceAtLeast(0.0)
    val principalRemaining = (principal - principalPaid).coerceAtLeast(0.0)
    
    val totalPaid = interestPaid + principalPaid
    val progress = if (totalDue > 0) (totalPaid / totalDue).toFloat().coerceIn(0f, 1f) else 0f
    
    return LoanBreakdown(
        totalInterest = totalInterest,
        interestRemaining = interestRemaining,
        principalRemaining = principalRemaining,
        interestPaid = interestPaid,
        principalPaid = principalPaid,
        totalDue = totalDue,
        progress = progress
    )
}

@Composable
fun LoanItemEnhanced(
    loan: Loan,
    breakdown: LoanBreakdown,
    onDelete: () -> Unit,
    onRepay: (() -> Unit)?,
    isPaid: Boolean = false
) {
    var showDetails by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetails = !showDetails },
        colors = CardDefaults.cardColors(
            containerColor = if (isPaid) 
                Color(0xFF10B981).copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surface
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = if (isPaid) Color(0xFF10B981).copy(alpha = 0.2f) 
                               else MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = if (isPaid) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = formatCurrency(breakdown.totalDue),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = loan.purpose ?: loan.lender ?: "Prêt",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { showDetails = !showDetails }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (showDetails) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Détails",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (onRepay != null) {
                        IconButton(onClick = onRepay, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Payment, contentDescription = "Rembourser", tint = Color(0xFF10B981))
                        }
                    }
                    
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            AnimatedVisibility(visible = showDetails) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Text(
                        text = "Capital: ${formatCurrency(loan.principal)} + Intérêts: ${formatCurrency(breakdown.totalInterest)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Taux: ${String.format("%.2f", loan.interestRate)}% · Durée: ${loan.termMonths} mois",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🏦 Intérêts:", style = MaterialTheme.typography.bodySmall)
                                Text("${formatCurrency(breakdown.interestPaid)} / ${formatCurrency(breakdown.totalInterest)}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Restant: ${formatCurrency(breakdown.interestRemaining)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444))
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("💰 Capital:", style = MaterialTheme.typography.bodySmall)
                                Text("${formatCurrency(breakdown.principalPaid)} / ${formatCurrency(loan.principal)}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Restant: ${formatCurrency(breakdown.principalRemaining)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444))
                        }
                    }
                }
            }

            if (!isPaid) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Restant: ${formatCurrency(breakdown.interestRemaining + breakdown.principalRemaining)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                    Text("${(breakdown.progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = breakdown.progress,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF10B981),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer ce prêt et tous ses remboursements ?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun RepaymentDialog(
    loan: Loan,
    breakdown: LoanBreakdown,
    onDismiss: () -> Unit,
    onConfirm: (interestAmount: Double, principalAmount: Double) -> Unit
) {
    var interestAmount by remember { mutableStateOf("") }
    var principalAmount by remember { mutableStateOf("") }
    
    // Couleurs pour les champs de texte
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF1E293B),
        unfocusedTextColor = Color(0xFF1E293B),
        cursorColor = Color(0xFF10B981),
        focusedBorderColor = Color(0xFF10B981),
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedLabelColor = Color(0xFF10B981),
        unfocusedLabelColor = Color(0xFF64748B),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF1E293B),
        title = { 
            Text(
                "Remboursement - ${loan.purpose ?: loan.lender ?: "Prêt"}",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Informations du prêt :", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("Capital: ${formatCurrency(loan.principal)} · Intérêt: ${loan.interestRate}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        Text("Intérêts restants: ${formatCurrency(breakdown.interestRemaining)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444))
                        Text("Capital restant: ${formatCurrency(breakdown.principalRemaining)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444))
                    }
                }
                
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFD4AF37).copy(alpha = 0.1f))) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💡")
                        Text("Ce remboursement sera enregistré comme une dépense.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF856404))
                    }
                }
                
                OutlinedTextField(
                    value = interestAmount,
                    onValueChange = { interestAmount = it },
                    label = { Text("Intérêts (max ${formatCurrency(breakdown.interestRemaining)})") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                
                OutlinedTextField(
                    value = principalAmount,
                    onValueChange = { principalAmount = it },
                    label = { Text("Capital (max ${formatCurrency(breakdown.principalRemaining)})") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                
                val interest = interestAmount.toDoubleOrNull() ?: 0.0
                val principal = principalAmount.toDoubleOrNull() ?: 0.0
                val total = interest + principal
                
                if (total > 0) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.15f)), shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Récapitulatif :", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            Text("Intérêts: ${formatCurrency(interest)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1E293B))
                            Text("Capital: ${formatCurrency(principal)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1E293B))
                            Text("Total: ${formatCurrency(total)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val interest = interestAmount.toDoubleOrNull() ?: 0.0
                    val principal = principalAmount.toDoubleOrNull() ?: 0.0
                    if (interest + principal > 0) onConfirm(interest, principal)
                },
                enabled = (interestAmount.toDoubleOrNull() ?: 0.0) + (principalAmount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Confirmer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler", color = Color(0xFF64748B)) } }
    )
}

@Composable
fun AddLoanDialog(onDismiss: () -> Unit, onConfirm: (Loan) -> Unit) {
    var description by remember { mutableStateOf("") }
    var principal by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("0") }
    var termMonths by remember { mutableStateOf("") }
    
    // Couleurs pour les champs de texte
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF1E293B),
        unfocusedTextColor = Color(0xFF1E293B),
        cursorColor = Color(0xFF10B981),
        focusedBorderColor = Color(0xFF10B981),
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedLabelColor = Color(0xFF10B981),
        unfocusedLabelColor = Color(0xFF64748B),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF1E293B),
        title = { 
            Text(
                "Nouveau prêt",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = principal, 
                    onValueChange = { principal = it }, 
                    label = { Text("Montant principal (FCFA)") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = interestRate, 
                    onValueChange = { interestRate = it }, 
                    label = { Text("Taux d'intérêt (%)") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = termMonths, 
                    onValueChange = { termMonths = it }, 
                    label = { Text("Durée (mois)") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = description, 
                    onValueChange = { description = it }, 
                    label = { Text("Description") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (principal.isNotBlank()) {
                        onConfirm(Loan(
                            principal = principal.toDoubleOrNull() ?: 0.0,
                            interestRate = interestRate.toDoubleOrNull() ?: 0.0,
                            termMonths = termMonths.toIntOrNull() ?: 0,
                            startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            lender = null,
                            purpose = description.ifBlank { null }
                        ))
                    }
                },
                enabled = principal.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Créer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler", color = Color(0xFF64748B)) } }
    )
}
