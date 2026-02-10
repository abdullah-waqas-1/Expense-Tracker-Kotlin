package com.example.expensetracker.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.R
import com.example.expensetracker.presentation.viewmodel.ExpenseListViewModel
import com.example.expensetracker.presentation.ui.component.*
import com.example.expensetracker.ui.theme.LocalThemeManager
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
    val themeManager = LocalThemeManager.current
    val expenses by viewModel.expenses.collectAsState()
    val stats by viewModel.expenseStats.collectAsState()
    val error by viewModel.error.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(stringResource(R.string.dialog_clear_all_title)) },
            text = { Text(stringResource(R.string.dialog_clear_all_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllExpenses()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.action_clear_all), color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.title_expense_tracker),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = { themeManager.toggleTheme() }) {
                        Icon(
                            imageVector = if (themeManager.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = stringResource(R.string.cd_toggle_theme)
                        )
                    }
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            Icons.Default.FilterList,
                            stringResource(R.string.cd_filter),
                            tint = if (searchQuery.isNotEmpty() || selectedCategory != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.Analytics, stringResource(R.string.cd_stats))
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, stringResource(R.string.menu_more_options))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_clear_all_data), color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    showDeleteAllDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.action_add))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.action_add_transaction), fontWeight = FontWeight.SemiBold)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            BalanceCard(stats.balance, stats.totalIncome, stats.totalExpenses)

            AnimatedVisibility(visible = error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            ExpenseFilterSection(
                visible = showFilters,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setCategory(it) }
            )

            if (expenses.isEmpty()) {
                EmptyStateView(Modifier.weight(1f))
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

@Composable
private fun EmptyStateView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.AutoMirrored.Filled.ReceiptLong,
                null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.empty_state_message),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}