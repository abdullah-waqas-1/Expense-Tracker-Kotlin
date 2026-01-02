package com.example.expensetracker.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.data.model.expenseCategories
import com.example.expensetracker.data.model.incomeCategories
import com.example.expensetracker.presentation.viewmodel.AddEditExpenseViewModel
import com.example.expensetracker.ui.theme.*
import com.example.expensetracker.presentation.ui.component.*
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
    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 16.dp, start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, DividerColor, RoundedCornerShape(12.dp)).clickable(onClick = onNavigateBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = if (uiState.isEditMode) "Edit Transaction" else "New Transaction", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(AppBackground).navigationBarsPadding().padding(16.dp).padding(bottom = 16.dp)) {
                Button(
                    onClick = { viewModel.saveExpense(onSuccess = onNavigateBack) },
                    enabled = uiState.title.isNotBlank() && uiState.amount.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp), spotColor = BrandPurple.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple, disabledContainerColor = Color.Gray.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Transaction", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White))
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(14.dp)).background(Color.White), verticalAlignment = Alignment.CenterVertically) {
                TypeTab(text = "Expense", isSelected = !uiState.isIncome, selectedColor = ExpenseRed, modifier = Modifier.weight(1f), onClick = { viewModel.updateIsIncome(false); if (uiState.isIncome) viewModel.updateCategory("Food") })
                TypeTab(text = "Income", isSelected = uiState.isIncome, selectedColor = IncomeGreen, modifier = Modifier.weight(1f), onClick = { viewModel.updateIsIncome(true); if (!uiState.isIncome) viewModel.updateCategory("Salary") })
            }

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Enter Amount", style = TextStyle(fontSize = 14.sp, color = TextSecondary))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text(text = if (uiState.isIncome) "+" else "-", style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = if (uiState.isIncome) IncomeGreen else ExpenseRed))
                    BasicTextField(
                        value = uiState.amount,
                        onValueChange = { viewModel.updateAmount(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center),
                        singleLine = true,
                        cursorBrush = SolidColor(BrandPurple),
                        modifier = Modifier.weight(1f, fill = false),
                        decorationBox = { innerTextField -> if (uiState.amount.isEmpty()) Text("0", style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)); innerTextField() }
                    )
                }
            }

            Column {
                Text("Select Category", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary), modifier = Modifier.padding(bottom = 12.dp))
                val categories = if (uiState.isIncome) incomeCategories else expenseCategories
                LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(180.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categories) { category ->
                        val isSelected = category.name == uiState.category
                        val categoryColor = Color(category.color)
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { viewModel.updateCategory(category.name) }) {
                            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(if (isSelected) categoryColor else categoryColor.copy(alpha = 0.1f)).border(width = if (isSelected) 0.dp else 1.dp, color = Color.Transparent, shape = RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                                Text(category.icon, style = TextStyle(fontSize = 24.sp))
                                if (isSelected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp).align(Alignment.TopEnd).padding(2.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = category.name, style = TextStyle(fontSize = 11.sp, color = if (isSelected) TextPrimary else TextSecondary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal), maxLines = 1)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(value = uiState.title, onValueChange = { viewModel.updateTitle(it) }, label = "Title", placeholder = "What is this for?")
                Row(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).clickable { showDatePicker = true }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(AppBackground), contentAlignment = Alignment.Center) { Icon(Icons.Default.CalendarMonth, null, tint = BrandPurple, modifier = Modifier.size(20.dp)) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column { Text("Date", style = TextStyle(fontSize = 12.sp, color = TextSecondary)); Text(dateFormat.format(uiState.date), style = TextStyle(fontSize = 16.sp, color = TextPrimary)) }
                }
                CustomTextField(value = uiState.description, onValueChange = { viewModel.updateDescription(it) }, label = "Note", placeholder = "Add a description...", icon = Icons.Default.Description)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { viewModel.updateDate(it); showDatePicker = false },
            onDismiss = { showDatePicker = false },
            initialDate = uiState.date
        )
    }
}