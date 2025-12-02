package com.mbongo.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PieChartEntry(
    val label: String,
    val value: Float,
    val color: Color,
    val icon: String = ""
)

@Composable
fun PieChart(
    entries: List<PieChartEntry>,
    modifier: Modifier = Modifier,
    centerText: String? = null,
    showLabels: Boolean = true
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "pieAnimation"
    )
    
    LaunchedEffect(entries) {
        animationPlayed = true
    }
    
    val total = entries.sumOf { it.value.toDouble() }.toFloat()
    
    if (total <= 0) {
        Box(
            modifier = modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucune donnée",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 40f
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)
                
                var startAngle = -90f
                
                entries.forEach { entry ->
                    val sweepAngle = (entry.value / total) * 360f * animatedProgress
                    
                    drawArc(
                        color = entry.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    
                    startAngle += sweepAngle
                }
            }
            
            centerText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        if (showLabels && entries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Légende
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                entries.take(6).forEach { entry ->
                    val percentage = if (total > 0) (entry.value / total * 100).toInt() else 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Canvas(modifier = Modifier.size(12.dp)) {
                                drawCircle(color = entry.color)
                            }
                            Text(
                                text = "${entry.icon} ${entry.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (entries.size > 6) {
                    Text(
                        text = "... et ${entries.size - 6} autres",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
            }
        }
    }
}

// Pie chart simple pour revenus vs dépenses
@Composable
fun IncomeExpensePieChart(
    income: Double,
    expenses: Double,
    modifier: Modifier = Modifier
) {
    val usedClamped = minOf(expenses, income)
    val remaining = maxOf(income - usedClamped, 0.0)
    val usedPercent = if (income > 0) ((expenses / income) * 100).toInt().coerceIn(0, 100) else 0
    
    val entries = listOf(
        PieChartEntry("Dépenses", usedClamped.toFloat(), Color(0xFFEF4444)),
        PieChartEntry("Reste", remaining.toFloat(), Color(0xFF10B981))
    )
    
    PieChart(
        entries = entries,
        modifier = modifier,
        centerText = "$usedPercent%\nutilisé",
        showLabels = true
    )
}

// Bar chart horizontal pour l'évolution mensuelle
@Composable
fun MonthlyBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1.0
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { (month, value) ->
            val progress = if (maxValue > 0) (value / maxValue).toFloat() else 0f
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = month,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(36.dp)
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                ) {
                    // Background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = Color(0xFF374151),
                            size = size
                        )
                    }
                    
                    // Progress
                    if (progress > 0) {
                        var animationPlayed by remember { mutableStateOf(false) }
                        val animatedProgress by animateFloatAsState(
                            targetValue = if (animationPlayed) progress else 0f,
                            animationSpec = tween(durationMillis = 800),
                            label = "barAnimation"
                        )
                        
                        LaunchedEffect(Unit) {
                            animationPlayed = true
                        }
                        
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRoundRect(
                                color = Color(0xFF667EEA),
                                size = size.copy(width = size.width * animatedProgress)
                            )
                        }
                    }
                }
                
                Text(
                    text = formatCompact(value),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(50.dp)
                )
            }
        }
    }
}

private fun formatCompact(value: Double): String {
    return when {
        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000)
        value >= 1_000 -> String.format("%.0fK", value / 1_000)
        else -> String.format("%.0f", value)
    }
}
