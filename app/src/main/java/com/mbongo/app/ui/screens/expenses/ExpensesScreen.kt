@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.mbongo.app.ui.screens.expenses

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mbongo.app.data.local.entity.Expense
import com.mbongo.app.ui.viewmodel.ExpensesViewModel
import com.mbongo.app.ui.viewmodel.ExpenseResult
import com.mbongo.app.ui.viewmodel.ExpenseDisplay
import com.mbongo.app.ui.components.CopyrightFooter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navController: NavController,
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val hasSalary by viewModel.hasSalary.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showNoSalaryDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Observer les résultats d'ajout
    LaunchedEffect(Unit) {
        viewModel.addExpenseResult.collect { result ->
            when (result) {
                is ExpenseResult.Success -> {
                    showAddDialog = false
                }
                is ExpenseResult.Error -> {
                    showNoSalaryDialog = true
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    if (hasSalary) {
                        showAddDialog = true 
                    } else {
                        showNoSalaryDialog = true
                    }
                },
                containerColor = if (hasSalary) MaterialTheme.colorScheme.primary else Color.Gray
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter dépense")
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
                text = "Dépenses",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Alerte si pas de salaire
            if (!hasSalary) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚠️", style = MaterialTheme.typography.headlineSmall)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Salaire requis",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                            Text(
                                text = "Enregistrez d'abord un salaire pour ce mois",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB45309).copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Total Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Total dépenses",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%,.0f", totalExpenses)} FCFA",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expenses List
            if (expenses.isEmpty()) {
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
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Aucune dépense",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (hasSalary) "Appuyez sur + pour ajouter" else "Ajoutez d'abord un salaire",
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
                    items(expenses) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onDelete = { viewModel.deleteExpense(expense) }
                        )
                    }
                    
                    // Copyright Footer
                    item {
                        CopyrightFooter()
                    }
                }
            }
            
            // Copyright Footer si liste vide
            if (expenses.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                CopyrightFooter()
            }
        }

        if (showAddDialog) {
            AddExpenseDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { expense ->
                    viewModel.addExpense(expense)
                },
                viewModel = viewModel
            )
        }
        
        // Dialogue d'avertissement salaire requis
        if (showNoSalaryDialog) {
            AlertDialog(
                onDismissRequest = { showNoSalaryDialog = false },
                icon = { Text("💰", style = MaterialTheme.typography.headlineLarge) },
                title = { Text("Salaire requis") },
                text = { 
                    Text("Vous devez d'abord enregistrer un salaire pour ce mois avant de pouvoir ajouter des dépenses.")
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            showNoSalaryDialog = false
                            // TODO: Navigation vers écran revenus
                        }
                    ) {
                        Text("Ajouter un salaire")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoSalaryDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

@Composable
fun ExpenseItem(
    expense: ExpenseDisplay,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var showDeleteDialog by remember { mutableStateOf(false) }
    val parsedDate = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(expense.date)
    } catch (e: Exception) {
        null
    }

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
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = expense.description ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
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
                    text = "${String.format("%,.0f", expense.amount)} F",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
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
            text = { Text("Voulez-vous vraiment supprimer cette dépense ?") },
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
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (Expense) -> Unit,
    viewModel: ExpensesViewModel
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val categories by viewModel.categories.collectAsState()
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var expanded by remember { mutableStateOf(false) }
    
    // Couleurs pour les champs de texte - style web app
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
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        titleContentColor = Color(0xFF1E293B),
        title = { 
            Text(
                "Nouvelle dépense",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = Color(0xFF64748B)) },
                    placeholder = { Text("Ex: Restaurant, courses...", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                // Montant
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Montant (FCFA)", color = Color(0xFF64748B)) },
                    placeholder = { Text("0", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                // Sélecteur de catégorie - Style web app
                Column {
                    Text(
                        "Catégorie",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { expanded = true },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (expanded) Color(0xFF10B981) else Color(0xFFCBD5E1)
                            ),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val selectedCategory = categories.find { it.id == selectedCategoryId }
                                if (selectedCategory != null) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            selectedCategory.icon,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            selectedCategory.name,
                                            color = Color(0xFF1E293B),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Text(
                                        "Sélectionner une catégorie",
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }
                        
                        // Menu déroulant avec fond blanc
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(Color.White)
                                .heightIn(max = 300.dp)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Icône de catégorie
                                            Surface(
                                                modifier = Modifier.size(32.dp),
                                                shape = CircleShape,
                                                color = try {
                                                    Color(android.graphics.Color.parseColor(category.color))
                                                } catch (e: Exception) {
                                                    Color(0xFF10B981)
                                                }.copy(alpha = 0.15f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        category.icon,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                            Text(
                                                category.name,
                                                color = Color(0xFF1E293B),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        expanded = false
                                    },
                                    modifier = Modifier.background(
                                        if (selectedCategoryId == category.id) 
                                            Color(0xFF10B981).copy(alpha = 0.1f) 
                                        else 
                                            Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank() && amount.isNotBlank() && selectedCategoryId != null) {
                        onConfirm(
                            Expense(
                                description = description,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                categoryId = selectedCategoryId!!,
                                date = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
                            )
                        )
                    }
                },
                enabled = description.isNotBlank() && amount.isNotBlank() && selectedCategoryId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFCBD5E1),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Ajouter", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            }
        }
    )
}
