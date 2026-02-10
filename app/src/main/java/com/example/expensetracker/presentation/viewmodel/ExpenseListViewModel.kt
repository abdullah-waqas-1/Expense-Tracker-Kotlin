
package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.ExpenseStats
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _selectedDateRange = MutableStateFlow<Pair<Date?, Date?>>(null to null)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val searchQuery = _searchQuery.asStateFlow()
    val selectedCategory = _selectedCategory.asStateFlow()
    val selectedDateRange = _selectedDateRange.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val error = _error.asStateFlow()

    val expenses = combine(
        repository.getAllExpenses(),
        _selectedDateRange,
        _selectedCategory,
        _searchQuery
    ) { expenses, dateRange, category, query ->
        expenses.filter { expense ->
            val matchesDateRange = if (dateRange.first != null && dateRange.second != null) {
                expense.date >= dateRange.first && expense.date <= dateRange.second
            } else true

            val matchesCategory = category?.let { expense.category == it } ?: true

            val matchesSearch = query.isEmpty() ||
                    expense.title.contains(query, ignoreCase = true) ||
                    expense.description?.contains(query, ignoreCase = true) == true ||
                    expense.category.contains(query, ignoreCase = true)

            matchesDateRange && matchesCategory && matchesSearch
        }
    }.catch { throwable ->
        _error.value = "Failed to load expenses: ${throwable.message}"
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

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                repository.deleteExpense(expense)
            } catch (e: Exception) {
                _error.value = "Failed to delete expense: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearAllExpenses() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                repository.deleteAllExpenses()
            } catch (e: Exception) {
                _error.value = "Failed to clear transactions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setDateRange(startDate: Date?, endDate: Date?) {
        _selectedDateRange.value = startDate to endDate
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearError() {
        _error.value = null
    }

    fun clearAllFilters() {
        _selectedDateRange.value = null to null
        _selectedCategory.value = null
        _searchQuery.value = ""
    }
}
