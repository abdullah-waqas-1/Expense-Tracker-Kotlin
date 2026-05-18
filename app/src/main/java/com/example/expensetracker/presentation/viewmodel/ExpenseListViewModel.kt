package com.example.expensetracker.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.ThemePreferences
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.ExpenseStats
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.util.CsvExportHelper
import com.example.expensetracker.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _error = MutableStateFlow<UiText?>(null)

    val searchQuery = _searchQuery.asStateFlow()
    val selectedCategory = _selectedCategory.asStateFlow()
    val error = _error.asStateFlow()

    val isBalanceVisible = themePreferences.isBalanceVisible
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val expenses = combine(
        repository.getAllExpenses(),
        _selectedCategory,
        _searchQuery
    ) { expenses, category, query ->
        expenses.filter { expense ->
            val matchesCategory = category?.let { expense.category == it } ?: true

            val matchesSearch = query.isEmpty() ||
                    expense.title.contains(query, ignoreCase = true) ||
                    expense.description?.contains(query, ignoreCase = true) == true ||
                    expense.category.contains(query, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }.catch { throwable ->
        _error.value = UiText.DynamicString(throwable.message ?: throwable.toString())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val expenseStats = expenses.map { filteredList ->
        val totalIncome = filteredList.filter { it.isIncome }.sumOf { it.amount }
        val totalExpense = filteredList.filter { !it.isIncome }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val categoryBreakdown = filteredList
            .filter { !it.isIncome }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        ExpenseStats(
            totalExpenses = totalExpense,
            totalIncome = totalIncome,
            balance = balance,
            categoryBreakdown = categoryBreakdown
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ExpenseStats(0.0, 0.0, 0.0, emptyMap())
    )

    fun toggleBalanceVisibility() {
        viewModelScope.launch {
            themePreferences.saveBalanceVisibility(!isBalanceVisible.value)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                _error.value = null
                repository.deleteExpense(expense)
            } catch (e: Exception) {
                _error.value = UiText.DynamicString(e.message ?: e.toString())
            }
        }
    }

    fun clearAllExpenses() {
        viewModelScope.launch {
            try {
                _error.value = null
                repository.deleteAllExpenses()
            } catch (e: Exception) {
                _error.value = UiText.DynamicString(e.message ?: e.toString())
            }
        }
    }

    fun exportAllData(context: Context) {
        viewModelScope.launch {
            val allExpenses = repository.getAllExpenses().first()
            if (allExpenses.isNotEmpty()) {
                CsvExportHelper.exportExpensesToCsv(context, allExpenses)
            }
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}