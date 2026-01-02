package com.example.expensetracker.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.data.model.expenseCategories
import com.example.expensetracker.data.model.incomeCategories
import com.example.expensetracker.presentation.viewmodel.ExpenseListViewModel
import com.example.expensetracker.ui.theme.*
import com.example.expensetracker.presentation.ui.component.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (Long) -> Unit,
    onNavigateToStats: () -> Unit,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val stats by viewModel.expenseStats.collectAsState()
    val error by viewModel.error.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = { Text("Expense Tracker", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground, titleContentColor = TextPrimary, actionIconContentColor = TextPrimary),
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, "Filter", tint = if (searchQuery.isNotEmpty() || selectedCategory != null) BrandPurple else TextPrimary)
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.Analytics, "Stats", tint = TextPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = BrandPurple,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Transaction", fontWeight = FontWeight.SemiBold)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            BalanceCard(stats.balance, stats.totalIncome, stats.totalExpenses)

            AnimatedVisibility(visible = error != null) {
                Card(colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.1f)), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(text = error ?: "", color = ExpenseRed, modifier = Modifier.padding(16.dp))
                }
            }

            AnimatedVisibility(visible = showFilters, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search transactions...", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = BrandPurple) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurple, unfocusedBorderColor = DividerColor, cursorColor = BrandPurple, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { CustomFilterChip(selected = selectedCategory == null, label = "All", onClick = { viewModel.setCategory(null) }) }
                            items(expenseCategories + incomeCategories) { category ->
                                CustomFilterChip(
                                    selected = selectedCategory == category.name,
                                    label = "${category.icon} ${category.name}",
                                    onClick = { if (selectedCategory == category.name) viewModel.setCategory(null) else viewModel.setCategory(category.name) }
                                )
                            }
                        }
                    }
                }
            }

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReceiptLong, null, modifier = Modifier.size(80.dp), tint = TextSecondary.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No transactions yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = expenses, key = { it.id }) { expense ->
                        ExpenseItem(
                            expense = expense,
                            dateFormat = dateFormat,
                            onClick = { onNavigateToEditExpense(expense.id) },
                            onDelete = { viewModel.deleteExpense(expense) },
                            modifier = Modifier.animateItem(placementSpec = tween(durationMillis = 300))
                        )
                    }
                }
            }
        }
    }
}