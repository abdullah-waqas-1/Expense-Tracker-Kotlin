package com.example.expensetracker.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.R
import com.example.expensetracker.presentation.viewmodel.AddEditExpenseViewModel
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SecondaryHeader(
                title = if (uiState.isEditMode)
                    stringResource(R.string.title_edit_transaction)
                else
                    stringResource(R.string.title_new_transaction),
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = { viewModel.saveExpense(onSuccess = onNavigateBack) },
                    enabled = uiState.title.isNotBlank() && uiState.amount.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            8.dp,
                            RoundedCornerShape(16.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        stringResource(R.string.action_save),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TypeTab(
                    text = stringResource(R.string.tab_expense),
                    isSelected = !uiState.isIncome,
                    selectedColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.updateIsIncome(false)
                        if (uiState.isIncome) viewModel.updateCategory("Food")
                    }
                )
                TypeTab(
                    text = stringResource(R.string.tab_income),
                    isSelected = uiState.isIncome,
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.updateIsIncome(true)
                        if (!uiState.isIncome) viewModel.updateCategory("Salary")
                    }
                )
            }

            AmountInputSection(
                amount = uiState.amount,
                onAmountChange = { viewModel.updateAmount(it) },
                isIncome = uiState.isIncome
            )

            CategorySelectionGrid(
                selectedCategory = uiState.category,
                onCategorySelected = { viewModel.updateCategory(it) },
                isIncome = uiState.isIncome
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = stringResource(R.string.label_title),
                    placeholder = stringResource(R.string.hint_title)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            stringResource(R.string.label_date),
                            style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            dateFormat.format(uiState.date),
                            style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }

                CustomTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = stringResource(R.string.label_note),
                    placeholder = stringResource(R.string.hint_note),
                    icon = Icons.Default.Description
                )
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