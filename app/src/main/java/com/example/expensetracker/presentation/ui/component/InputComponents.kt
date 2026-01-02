package com.example.expensetracker.presentation.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.theme.*

@Composable
fun CustomFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) BrandPurple else AppBackground,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
        modifier = Modifier.height(36.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = if (selected) Color.White else TextPrimary)
        }
    }
}

@Composable
fun TypeTab(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(300), label = "color"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else TextSecondary,
        animationSpec = tween(300), label = "text"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor))
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = BrandPurple, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(label, style = TextStyle(fontSize = 12.sp, color = TextSecondary))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(placeholder, style = TextStyle(fontSize = 16.sp, color = Color.LightGray))
                        }
                        innerTextField()
                    }
                )
            }
        }
    }
}