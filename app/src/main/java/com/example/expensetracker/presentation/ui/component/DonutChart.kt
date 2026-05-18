package com.example.expensetracker.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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

    val trackColor = MaterialTheme.colorScheme.background
    val fallbackSegmentColor = MaterialTheme.colorScheme.outline
    val emptyStateTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Box(modifier = Modifier.size(280.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(-90f)
        ) {
            val strokeWidthPx = thickness.toPx()

            val sharedRoundStroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            val sharedButtStroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)

            drawCircle(
                color = trackColor,
                style = sharedRoundStroke
            )

            if (totalAmount > 0) {
                var startAngle = 0f
                data.forEach { (category, amount) ->
                    val sweepAngle = (amount.toFloat() / totalAmount.toFloat()) * 360f * animationProgress

                    drawArc(
                        color = colors[category] ?: fallbackSegmentColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = sharedButtStroke
                    )
                    startAngle += sweepAngle
                }
            } else {
                drawCircle(
                    color = emptyStateTrackColor,
                    style = sharedRoundStroke
                )
            }
        }
    }
}