package com.mbongo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearSelector(
    selectedMonth: Int,
    selectedYear: Int,
    viewMode: String, // "month" ou "year"
    onMonthYearChange: (Int, Int) -> Unit,
    onViewModeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSelectedYear by remember(selectedYear) { mutableStateOf(selectedYear) }
    var currentSelectedMonth by remember(selectedMonth) { mutableStateOf(selectedMonth) }
    var showYearDropdown by remember { mutableStateOf(false) }
    var showMonthDropdown by remember { mutableStateOf(false) }
    
    val months = listOf(
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    )
    
    val years = (2023..2026).toList()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Toggle vue mensuelle/annuelle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = viewMode == "month",
                    onClick = { onViewModeChange("month") },
                    label = { Text("Vue mensuelle") },
                    leadingIcon = {
                        if (viewMode == "month") {
                            Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp))
                        }
                    }
                )
                FilterChip(
                    selected = viewMode == "year",
                    onClick = { onViewModeChange("year") },
                    label = { Text("Vue annuelle") },
                    leadingIcon = {
                        if (viewMode == "year") {
                            Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp))
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Sélecteurs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sélecteur de mois (si vue mensuelle)
                if (viewMode == "month") {
                    ExposedDropdownMenuBox(
                        expanded = showMonthDropdown,
                        onExpandedChange = { showMonthDropdown = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = months[currentSelectedMonth - 1],
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Mois") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMonthDropdown) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = showMonthDropdown,
                            onDismissRequest = { showMonthDropdown = false }
                        ) {
                            months.forEachIndexed { index, name ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        currentSelectedMonth = index + 1
                                        showMonthDropdown = false
                                        onMonthYearChange(currentSelectedMonth, currentSelectedYear)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Sélecteur d'année
                ExposedDropdownMenuBox(
                    expanded = showYearDropdown,
                    onExpandedChange = { showYearDropdown = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = currentSelectedYear.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Année") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showYearDropdown) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showYearDropdown,
                        onDismissRequest = { showYearDropdown = false }
                    ) {
                        years.forEach { yr ->
                            DropdownMenuItem(
                                text = { Text(yr.toString()) },
                                onClick = {
                                    currentSelectedYear = yr
                                    showYearDropdown = false
                                    onMonthYearChange(currentSelectedMonth, currentSelectedYear)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
