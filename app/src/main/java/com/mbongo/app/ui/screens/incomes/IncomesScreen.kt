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
import com.mbongo.app.ui.components.CopyrightFooter
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
    var showAddDialog by remember { mutableStateOf(false) }

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
            // Header
            Text(
                text = "Revenus",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Total revenus",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%,.0f", totalIncomes)} FCFA",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Incomes List
            if (incomes.isEmpty()) {
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
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Aucun revenu",
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
    income: Income,
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    shape = CircleShape,
                    color = if (isSalary) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.tertiaryContainer
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        if (isSalary) {
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "SALAIRE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = dateFormat.format(parsedDate ?: Date()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer ce revenu ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
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
    val categories by viewModel.categories.collectAsState()
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var expanded by remember { mutableStateOf(false) }
    
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
                "Nouveau revenu",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type de revenu (Salaire / Autre)
                Text(
                    text = "Type de revenu",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF64748B)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = incomeType == "salary",
                        onClick = { incomeType = "salary" },
                        label = { Text("💰 Salaire", color = if (incomeType == "salary") Color(0xFF10B981) else Color(0xFF64748B)) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.15f),
                            containerColor = Color(0xFFF1F5F9)
                        )
                    )
                    FilterChip(
                        selected = incomeType == "other",
                        onClick = { incomeType = "other" },
                        label = { Text("💵 Autre", color = if (incomeType == "other") Color(0xFF667EEA) else Color(0xFF64748B)) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF667EEA).copy(alpha = 0.15f),
                            containerColor = Color(0xFFF1F5F9)
                        )
                    )
                }
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (incomeType == "salary") "Source (ex: Employeur)" else "Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Montant (FCFA)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.id == selectedCategoryId }?.name ?: "Sélectionner",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Catégorie") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name, color = Color(0xFF1E293B)) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // Note si type = salaire
                if (incomeType == "salary") {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ℹ️")
                            Text(
                                text = "Le salaire est requis pour pouvoir enregistrer des dépenses ce mois.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF047857)
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
                enabled = description.isNotBlank() && amount.isNotBlank() && selectedCategoryId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color(0xFF64748B))
            }
        }
    )
}
