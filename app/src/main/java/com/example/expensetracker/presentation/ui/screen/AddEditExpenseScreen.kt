package com.example.expensetracker.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.data.model.expenseCategories
import com.example.expensetracker.data.model.incomeCategories
import com.example.expensetracker.presentation.viewmodel.AddEditExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit ${if (uiState.isIncome) "Income" else "Expense"}" else "Add ${if (uiState.isIncome) "Income" else "Expense"}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveExpense(onSuccess = onNavigateBack)
                        },
                        enabled = uiState.title.isNotBlank() && uiState.amount.isNotBlank(),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Income/Expense Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FilterChip(
                            selected = !uiState.isIncome,
                            onClick = {
                                viewModel.updateIsIncome(false)
                                // Reset category when switching type
                                if (uiState.isIncome) {
                                    viewModel.updateCategory("Other")
                                }
                            },
                            label = { Text("Expense") },
                            leadingIcon = if (!uiState.isIncome) {
                                { Icon(Icons.Default.Check, "Selected") }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF5252).copy(alpha = 0.2f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = uiState.isIncome,
                            onClick = {
                                viewModel.updateIsIncome(true)
                                // Reset category when switching type
                                if (!uiState.isIncome) {
                                    viewModel.updateCategory("Other")
                                }
                            },
                            label = { Text("Income") },
                            leadingIcon = if (uiState.isIncome) {
                                { Icon(Icons.Default.Check, "Selected") }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }

            // Title Field
            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title *") },
                    placeholder = { Text("Enter ${if (uiState.isIncome) "income" else "expense"} title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Title, "Title") },
                    isError = uiState.error?.contains("title", ignoreCase = true) == true,
//                    supportingText = if (uiState.title.isBlank()) {
//                        { Text("Required field", color = MaterialTheme.colorScheme.error) }
//                    } else null
                )
            }

            // Amount Field
            item {
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { newValue ->
                        // Only allow numeric input with decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            viewModel.updateAmount(newValue)
                        }
                    },
                    label = { Text("Amount *") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Icon(Icons.Default.AttachMoney, "Amount") },
                    //prefix = { Text("$") },
                    isError = uiState.error?.contains("amount", ignoreCase = true) == true,
//                    supportingText = if (uiState.amount.isBlank()) {
//                        { Text("Required field", color = MaterialTheme.colorScheme.error) }
//                    } else if (uiState.amount.toDoubleOrNull()?.let { it <= 0 } == true) {
//                        { Text("Amount must be greater than 0", color = MaterialTheme.colorScheme.error) }
//                    } else null
                )
            }

            // Category Selection
            item {
                OutlinedTextField(
                    value = uiState.category,
                    onValueChange = {},
                    label = { Text("Category") },
                    placeholder = { Text("Select category") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryDialog = true },
                    enabled = false,
                    readOnly = true,
                    leadingIcon = {
                        Text(
                            text = getCategoryIcon(uiState.category),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Select") },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Date Selection
            item {
                OutlinedTextField(
                    value = dateFormat.format(uiState.date),
                    onValueChange = {},
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.DateRange, "Date") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Select") },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Description Field
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("Add notes about this ${if (uiState.isIncome) "income" else "expense"}") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    leadingIcon = { Icon(Icons.Default.Description, "Description") }
                )
            }

            // Error Message
            item {
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    uiState.error?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Add spacing at the bottom
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Category Selection Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(
                    "Select ${if (uiState.isIncome) "Income" else "Expense"} Category",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    val categories = if (uiState.isIncome) incomeCategories else expenseCategories
                    items(categories) { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable {
                                    viewModel.updateCategory(category.name)
                                    showCategoryDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (category.name == uiState.category) {
                                    Color(category.color).copy(alpha = 0.3f)
                                } else {
                                    Color(category.color).copy(alpha = 0.1f)
                                }
                            ),
                            border = if (category.name == uiState.category) {
                                BorderStroke(2.dp, Color(category.color))
                            } else null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = category.icon,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Enhanced Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                viewModel.updateDate(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = uiState.date
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Date = Date()
) {
    val calendar = Calendar.getInstance()
    calendar.time = initialDate

    var selectedDate by remember { mutableStateOf(calendar.time) }
    val dateFormatter = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Selected Date Display
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = dateFormatter.format(selectedDate),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Quick Date Selection Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val today = Calendar.getInstance().time
                    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time
                    val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) }.time

                    listOf(
                        "Today" to today,
                        "Yesterday" to yesterday,
                        "Week ago" to weekAgo
                    ).forEach { (label, date) ->
                        FilterChip(
                            selected = isSameDay(selectedDate, date),
                            onClick = { selectedDate = date },
                            label = { Text(label, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }

                // Simple Calendar View
                SimpleDatePicker(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onDateSelected(selectedDate) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleDatePicker(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate

    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    val monthNames = remember {
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    }

    Column(modifier = modifier) {
        // Month/Year selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentMonth == 0) {
                        currentMonth = 11
                        currentYear--
                    } else {
                        currentMonth--
                    }
                }
            ) {
                Icon(Icons.Default.ChevronLeft, "Previous month")
            }

            Text(
                text = "${monthNames[currentMonth]} $currentYear",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            IconButton(
                onClick = {
                    if (currentMonth == 11) {
                        currentMonth = 0
                        currentYear++
                    } else {
                        currentMonth++
                    }
                }
            ) {
                Icon(Icons.Default.ChevronRight, "Next month")
            }
        }

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        LazyColumn(
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val dates = generateDatesForMonth(currentYear, currentMonth)
            items(dates.chunked(7)) { weekDates ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weekDates.forEach { date ->
                        val isSelected = isSameDay(date, selectedDate)
                        val isToday = isSameDay(date, Date())
                        val dayCalendar = Calendar.getInstance().apply { time = date }
                        val isCurrentMonth = dayCalendar.get(Calendar.MONTH) == currentMonth

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    isToday -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun generateDatesForMonth(year: Int, month: Int): List<Date> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)

    // Get first day of month and find the starting Sunday
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    calendar.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))

    val dates = mutableListOf<Date>()

    // Generate 42 days (6 weeks) to fill the calendar grid
    repeat(42) {
        dates.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    return dates
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

private fun getCategoryIcon(category: String): String {
    return when(category) {
        "Food" -> "🍔"
        "Transport" -> "🚗"
        "Shopping" -> "🛍️"
        "Entertainment" -> "🎮"
        "Bills" -> "📄"
        "Healthcare" -> "🏥"
        "Education" -> "📚"
        "Salary" -> "💰"
        "Freelance" -> "💻"
        "Investment" -> "📈"
        "Other" -> "📌"
        else -> "📌"
    }
}