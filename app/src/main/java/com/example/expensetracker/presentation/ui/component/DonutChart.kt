package com.example.expensetracker.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.expenseCategories
import com.example.expensetracker.ui.theme.AppBackground

@Composable
fun DonutChart(
    data: Map<String, Double>,
    totalAmount: Double,
    animationProgress: Float,
    thickness: Dp = 35.dp
) {
    val colors = remember(data) {
        data.keys.associateWith { categoryName ->
            val catColor = expenseCategories.find { it.name == categoryName }?.color
                ?: 0xFFCCCCCC
            Color(catColor)
        }
    }

    Box(modifier = Modifier.size(280.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(-90f)
        ) {
            drawCircle(
                color = AppBackground,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
            )

            if (totalAmount > 0) {
                var startAngle = 0f
                data.forEach { (category, amount) ->
                    val sweepAngle = (amount.toFloat() / totalAmount.toFloat()) * 360f * animationProgress

                    drawArc(
                        color = colors[category] ?: Color.Gray,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            } else {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}