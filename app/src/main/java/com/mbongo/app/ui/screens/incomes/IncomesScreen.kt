@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.mbongo.app.ui.screens.incomes

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mbongo.app.data.local.entity.Income
import com.mbongo.app.ui.viewmodel.IncomesViewModel
import com.mbongo.app.ui.viewmodel.IncomeDisplay
import com.mbongo.app.ui.components.CopyrightFooter
import com.mbongo.app.data.sync.SyncStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomesScreen(
    navController: NavController,
    viewModel: IncomesViewModel = hiltViewModel()
) {
    val incomes by viewModel.incomes.collectAsState()
    val totalIncomes by viewModel.totalIncomes.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val isServerAvailable by viewModel.isServerAvailable.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Rafraîchir les données à chaque fois que l'écran devient visible
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter revenu")
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
            // Header avec état de sync
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Revenus",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                
                // Indicateur de synchronisation
                SyncIndicator(
                    isServerAvailable = isServerAvailable,
                    syncStatus = syncState.status,
                    pendingCount = syncState.pendingCount,
                    onSyncClick = { viewModel.forceSync() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Card - thème sombre
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total revenus",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%,.0f", totalIncomes)} FCFA",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF10B981), // Vert succès
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Icône
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Incomes List
            if (incomes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
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
                            Text(
                                text = "💰",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                text = "Aucun revenu",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                            Text(
                                text = "Appuyez sur + pour ajouter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(incomes) { income ->
                        IncomeItem(
                            income = income,
                            onDelete = { viewModel.deleteIncome(income) }
                        )
                    }
                    
                    // Copyright Footer
                    item {
                        CopyrightFooter()
                    }
                }
            }
            
            // Copyright Footer si liste vide
            if (incomes.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                CopyrightFooter()
            }
        }

        if (showAddDialog) {
            AddIncomeDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { income ->
                    viewModel.addIncome(income)
                    showAddDialog = false
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun IncomeItem(
    income: IncomeDisplay,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var showDeleteDialog by remember { mutableStateOf(false) }
    val parsedDate = try {
        if (!income.date.isNullOrBlank()) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(income.date)
        } else {
            SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(income.month)
        }
    } catch (e: Exception) {
        null
    }
    
    val isSalary = income.type == "salary"

    val goldColor = Color(0xFFD4AF37)
    val successGreen = Color(0xFF10B981)
    val darkCard = Color(0xFF2A2A2A)
    val mediumGray = Color(0xFF3A3A3A)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = darkCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSalary) 
                        successGreen.copy(alpha = 0.2f)
                    else 
                        goldColor.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isSalary) "💰" else "💵",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = income.description ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        if (isSalary) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = successGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "SALAIRE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = successGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = dateFormat.format(parsedDate ?: Date()),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "+${String.format("%,.0f", income.amount)} F",
                    style = MaterialTheme.typography.titleMedium,
                    color = successGreen,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = goldColor
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF2A2A2A),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("Confirmer la suppression", color = goldColor, fontWeight = FontWeight.Bold) },
            text = { Text("Voulez-vous vraiment supprimer ce revenu ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Supprimer", color = goldColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler", color = Color(0xFFCCCCCC))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Income) -> Unit,
    viewModel: IncomesViewModel
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var incomeType by remember { mutableStateOf("salary") } // "salary" ou "other"
    
    // Couleurs du thème sombre
    val goldColor = Color(0xFFD4AF37)
    val darkBackground = Color(0xFF2A2A2A)
    val mediumGray = Color(0xFF3A3A3A)
    val lightGray = Color(0xFFCCCCCC)
    val successGreen = Color(0xFF10B981)
    val purpleColor = Color(0xFF667EEA)
    
    // Couleurs pour les champs de texte - thème sombre
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = goldColor,
        focusedBorderColor = goldColor,
        unfocusedBorderColor = mediumGray,
        focusedLabelColor = goldColor,
        unfocusedLabelColor = lightGray,
        focusedContainerColor = mediumGray,
        unfocusedContainerColor = mediumGray,
        focusedPlaceholderColor = lightGray.copy(alpha = 0.5f),
        unfocusedPlaceholderColor = lightGray.copy(alpha = 0.5f)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        containerColor = darkBackground,
        titleContentColor = Color.White,
        title = { 
            Text(
                "Nouveau revenu",
                fontWeight = FontWeight.Bold,
                color = goldColor
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Type de revenu (Salaire / Autre)
                Column {
                    Text(
                        text = "Type de revenu",
                        style = MaterialTheme.typography.labelMedium,
                        color = lightGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Bouton Salaire
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { incomeType = "salary" },
                            shape = RoundedCornerShape(12.dp),
                            color = if (incomeType == "salary") successGreen.copy(alpha = 0.2f) else mediumGray,
                            border = if (incomeType == "salary") 
                                androidx.compose.foundation.BorderStroke(2.dp, successGreen) 
                            else androidx.compose.foundation.BorderStroke(1.dp, mediumGray)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💰 ", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Salaire",
                                    color = if (incomeType == "salary") successGreen else lightGray,
                                    fontWeight = if (incomeType == "salary") FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                        
                        // Bouton Autre
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { incomeType = "other" },
                            shape = RoundedCornerShape(12.dp),
                            color = if (incomeType == "other") purpleColor.copy(alpha = 0.2f) else mediumGray,
                            border = if (incomeType == "other") 
                                androidx.compose.foundation.BorderStroke(2.dp, purpleColor) 
                            else androidx.compose.foundation.BorderStroke(1.dp, mediumGray)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💵 ", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Autre",
                                    color = if (incomeType == "other") purpleColor else lightGray,
                                    fontWeight = if (incomeType == "other") FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (incomeType == "salary") "Source (ex: Employeur)" else "Description") },
                    placeholder = { Text(if (incomeType == "salary") "Nom de l'entreprise" else "Ex: Freelance, Prime...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                // Montant
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Montant (FCFA)") },
                    placeholder = { Text("0") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Note si type = salaire
                if (incomeType == "salary") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = successGreen.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ℹ️", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "Le salaire est requis pour pouvoir enregistrer des dépenses ce mois.",
                                style = MaterialTheme.typography.bodySmall,
                                color = successGreen
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank() && amount.isNotBlank()) {
                        onConfirm(
                            Income(
                                description = description,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                month = java.text.SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(java.util.Date()),
                                date = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date()),
                                type = incomeType
                            )
                        )
                    }
                },
                enabled = description.isNotBlank() && amount.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = goldColor,
                    contentColor = Color.Black,
                    disabledContainerColor = mediumGray,
                    disabledContentColor = lightGray
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Ajouter", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = lightGray, fontWeight = FontWeight.Medium)
            }
        }
    )
}

@Composable
fun SyncIndicator(
    isServerAvailable: Boolean,
    syncStatus: SyncStatus,
    pendingCount: Int,
    onSyncClick: () -> Unit
) {
    val goldColor = Color(0xFFD4AF37)
    val successGreen = Color(0xFF10B981)
    val warningOrange = Color(0xFFF59E0B)
    val grayColor = Color(0xFF6B7280)
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Indicateur de statut
        when {
            syncStatus == SyncStatus.SYNCING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = goldColor
                )
                Text(
                    text = "Sync...",
                    style = MaterialTheme.typography.labelSmall,
                    color = goldColor
                )
            }
            !isServerAvailable -> {
                Icon(
                    Icons.Default.CloudOff,
                    contentDescription = "Hors ligne",
                    modifier = Modifier.size(18.dp),
                    tint = grayColor
                )
                if (pendingCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = warningOrange
                    ) {
                        Text(
                            text = "$pendingCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            pendingCount > 0 -> {
                IconButton(
                    onClick = onSyncClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = "Synchroniser",
                        modifier = Modifier.size(18.dp),
                        tint = goldColor
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = goldColor
                ) {
                    Text(
                        text = "$pendingCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            else -> {
                Icon(
                    Icons.Default.CloudDone,
                    contentDescription = "Synchronisé",
                    modifier = Modifier.size(18.dp),
                    tint = successGreen
                )
            }
        }
    }
}
