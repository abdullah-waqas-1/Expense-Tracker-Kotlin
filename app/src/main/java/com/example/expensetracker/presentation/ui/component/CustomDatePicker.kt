package com.example.expensetracker.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Date = Date()
) {
    val calendar = Calendar.getInstance()
    calendar.time = initialDate
    var selectedDate by remember { mutableStateOf(calendar.time) }
    val dateFormatter = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Date", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary))
                Spacer(modifier = Modifier.height(8.dp))
                Text(dateFormatter.format(selectedDate), style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
                Spacer(modifier = Modifier.height(24.dp))

                SimpleDatePicker(selectedDate = selectedDate, onDateSelected = { selectedDate = it })

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onDateSelected(selectedDate) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Confirm", color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun SimpleDatePicker(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val monthNames = remember { listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December") }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { if (currentMonth == 0) { currentMonth = 11; currentYear-- } else currentMonth-- }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextSecondary)
            }
            Text("$currentYear ${monthNames[currentMonth]}", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
            IconButton(onClick = { if (currentMonth == 11) { currentMonth = 0; currentYear++ } else currentMonth++ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextSecondary, modifier = Modifier.rotate(180f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(day, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val dates = remember(currentMonth, currentYear) {
            val cal = Calendar.getInstance()
            cal.set(currentYear, currentMonth, 1)
            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            cal.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))
            val list = mutableListOf<Date>()
            repeat(42) { list.add(cal.time); cal.add(Calendar.DAY_OF_MONTH, 1) }
            list
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(dates) { date ->
                val dayCal = Calendar.getInstance().apply { time = date }
                val isSelected = isSameDay(date, selectedDate)
                val isCurrentMonth = dayCal.get(Calendar.MONTH) == currentMonth
                val isToday = isSameDay(date, Date())

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(when {
                            isSelected -> BrandPurple
                            isToday -> BrandPurple.copy(alpha = 0.1f)
                            else -> Color.Transparent
                        })
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        dayCal.get(Calendar.DAY_OF_MONTH).toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isSelected -> Color.White
                                !isCurrentMonth -> Color.LightGray
                                isToday -> BrandPurple
                                else -> TextPrimary
                            }
                        )
                    )
                }
            }
        }
    }
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}