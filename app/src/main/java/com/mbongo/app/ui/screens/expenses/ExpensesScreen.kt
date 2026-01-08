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

    // Rafraîchir les données à chaque fois que l'écran devient visible
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    // Observer les résultats d'ajout
    LaunchedEffect(Unit) {
        viewModel.addExpenseResult.collect { result ->
            when (result) {
                is ExpenseResult.Success -> {
                    showAddDialog = false
                    viewModel.refresh() // Rafraîchir après ajout réussi
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
                        containerColor = Color(0xFF3A3A3A)
                    ),
                    shape = RoundedCornerShape(16.dp)
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
                                text = "Revenu requis",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                text = "Enregistrez d'abord un revenu pour ce mois",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCCCCCC)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Total Card - Thème sombre
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
                            text = "Total dépenses",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFCCCCCC)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%,.0f", totalExpenses)} FCFA",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFFF59E0B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expenses List - État vide thème sombre
            if (expenses.isEmpty()) {
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
                                text = "🛒",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                text = "Aucune dépense",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (hasSalary) "Appuyez sur + pour ajouter" else "Ajoutez d'abord un salaire",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCCCCCC)
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
        
        // Dialogue d'avertissement revenu requis
        if (showNoSalaryDialog) {
            AlertDialog(
                onDismissRequest = { showNoSalaryDialog = false },
                containerColor = Color(0xFF2A2A2A),
                icon = { Text("💰", style = MaterialTheme.typography.headlineLarge) },
                title = { Text("Revenu requis", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold) },
                text = { 
                    Text(
                        "Vous devez d'abord enregistrer un revenu pour ce mois avant de pouvoir ajouter des dépenses.",
                        color = Color(0xFFCCCCCC)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            showNoSalaryDialog = false
                            // TODO: Navigation vers écran revenus
                        }
                    ) {
                        Text("Ajouter un revenu", color = Color(0xFFD4AF37))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoSalaryDialog = false }) {
                        Text("Annuler", color = Color(0xFFCCCCCC))
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
            containerColor = Color(0xFF2A2A2A)
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
                    shape = CircleShape,
                    color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = expense.description ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = dateFormat.format(parsedDate ?: Date()),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCCCCCC)
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
                    color = Color(0xFFF59E0B),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = Color(0xFFD4AF37)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF2A2A2A),
            title = { Text("Confirmer la suppression", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold) },
            text = { Text("Voulez-vous vraiment supprimer cette dépense ?", color = Color(0xFFCCCCCC)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Supprimer", color = Color(0xFFD4AF37))
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
    
    // Couleurs du thème sombre
    val goldColor = Color(0xFFD4AF37)
    val darkBackground = Color(0xFF2A2A2A)
    val mediumGray = Color(0xFF3A3A3A)
    val lightGray = Color(0xFFCCCCCC)
    val errorRed = Color(0xFFEF4444)
    
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
                "Nouvelle dépense",
                fontWeight = FontWeight.Bold,
                color = goldColor
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
                    label = { Text("Description") },
                    placeholder = { Text("Ex: Restaurant, courses...") },
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

                // Sélecteur de catégorie - Thème sombre
                Column {
                    Text(
                        "Catégorie",
                        style = MaterialTheme.typography.labelMedium,
                        color = lightGray,
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
                                if (expanded) goldColor else mediumGray
                            ),
                            color = mediumGray
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
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Text(
                                        "Sélectionner une catégorie",
                                        color = lightGray.copy(alpha = 0.5f)
                                    )
                                }
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = lightGray
                                )
                            }
                        }
                        
                        // Menu déroulant avec fond sombre
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(darkBackground)
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
                                                    goldColor
                                                }.copy(alpha = 0.2f)
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
                                                color = Color.White,
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
                                            goldColor.copy(alpha = 0.2f) 
                                        else 
                                            darkBackground
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
                        onDismiss()
                    }
                },
                enabled = description.isNotBlank() && amount.isNotBlank() && selectedCategoryId != null,
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
