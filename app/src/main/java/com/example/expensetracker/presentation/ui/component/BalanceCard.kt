package com.example.expensetracker.presentation.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.theme.*

@SuppressLint("DefaultLocale")
@Composable
fun BalanceCard(balance: Double, income: Double, expenses: Double) {
    var isVisible by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(BrandPurple, BrandPurpleDark)
                )
            )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(Color.White.copy(alpha = 0.1f), radius = 400f, center = Offset(size.width, 0f))
                drawCircle(Color.White.copy(alpha = 0.05f), radius = 300f, center = Offset(0f, size.height))
            }
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    // The Toggle Button
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Balance Visibility",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable { isVisible = !isVisible }
                    )
                }
                Text(
                    text = if (isVisible) "${String.format("%.2f", balance)} pkr" else "******",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatsItem(
                        label = "Income",
                        amount = income,
                        isVisible = isVisible,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        contentColor = Color(0xFFC8E6C9)
                    )
                    StatsItem(
                        label = "Expenses",
                        amount = expenses,
                        isVisible = isVisible,
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        contentColor = Color(0xFFFFCDD2)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsItem(label: String, amount: Double, icon: ImageVector, isVisible: Boolean, contentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(icon, null, tint = contentColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = if (isVisible) String.format("%.2f", amount) else "******",
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}