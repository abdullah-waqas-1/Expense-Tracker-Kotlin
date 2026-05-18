package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.R
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.util.AppDateUtils
import com.example.expensetracker.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val expenseId: Long = savedStateHandle.get<Long>("expenseId") ?: -1L

    private val _uiState = MutableStateFlow(AddEditExpenseUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (expenseId != 0L && expenseId != -1L) {
            loadExpense()
        }
    }

    private fun loadExpense() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                repository.getExpenseById(expenseId)?.let { expense ->
                    _uiState.value = _uiState.value.copy(
                        title = expense.title,
                        amount = expense.amount.toString(),
                        category = expense.category,
                        date = expense.date,
                        description = expense.description ?: "",
                        isIncome = expense.isIncome,
                        isEditMode = true,
                        isLoading = false
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = UiText.StringResource(R.string.error_expense_not_found),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.DynamicString(e.message ?: "Unknown database error"),
                    isLoading = false
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            error = if (title.isNotBlank()) null else _uiState.value.error
        )
    }

    fun updateAmount(amount: String) {
        val isValid = amount.isEmpty() ||
                amount.matches(Regex("^\\d*\\.?\\d*$")) &&
                amount.count { it == '.' } <= 1 &&
                amount.length <= 10

        if (isValid) {
            _uiState.value = _uiState.value.copy(
                amount = amount,
                error = if (amount.toDoubleOrNull()?.let { it > 0 } == true || amount.isEmpty())
                    null else _uiState.value.error
            )
        }
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateDate(date: Date) {
        val today = Date()
        val maxFutureDate = Date(today.time + (365L * 24 * 60 * 60 * 1000))
        val minDate = Date(today.time - (10L * 365 * 24 * 60 * 60 * 1000))

        if (date.after(minDate) && date.before(maxFutureDate)) {
            _uiState.value = _uiState.value.copy(date = date)
        }
    }

    fun updateDescription(description: String) {
        if (description.length <= 500) {
            _uiState.value = _uiState.value.copy(description = description)
        }
    }

    fun updateIsIncome(isIncome: Boolean) {
        _uiState.value = _uiState.value.copy(isIncome = isIncome)
    }

    fun saveExpense(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                _uiState.value = state.copy(isLoading = true, error = null)

                val validationError = validateInput(state)
                if (validationError != null) {
                    _uiState.value = state.copy(
                        error = validationError,
                        isLoading = false
                    )
                    return@launch
                }

                val normalizedDate = AppDateUtils.getStartOfDay(state.date)
                val amount = state.amount.toDouble()
                val expense = if (state.isEditMode) {
                    Expense(
                        id = expenseId,
                        title = state.title.trim(),
                        amount = amount,
                        category = state.category,
                        date = normalizedDate,
                        description = state.description.trim().ifBlank { null },
                        isIncome = state.isIncome
                    )
                } else {
                    Expense(
                        title = state.title.trim(),
                        amount = amount,
                        category = state.category,
                        date = normalizedDate,
                        description = state.description.trim().ifBlank { null },
                        isIncome = state.isIncome
                    )
                }

                if (state.isEditMode) {
                    repository.updateExpense(expense)
                } else {
                    repository.insertExpense(expense)
                }

                _uiState.value = state.copy(isLoading = false)
                onSuccess()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = UiText.DynamicString(e.message ?: "Failed to write data"),
                    isLoading = false
                )
            }
        }
    }

    private fun validateInput(state: AddEditExpenseUiState): UiText? {
        return when {
            state.title.isBlank() -> UiText.StringResource(R.string.err_title_required)
            state.title.length < 2 -> UiText.StringResource(R.string.err_title_too_short)
            state.title.length > 100 -> UiText.StringResource(R.string.err_title_too_long)
            state.amount.isBlank() -> UiText.StringResource(R.string.err_amount_required)
            state.amount.toDoubleOrNull() == null -> UiText.StringResource(R.string.err_amount_invalid)
            state.amount.toDouble() <= 0 -> UiText.StringResource(R.string.err_amount_zero)
            state.amount.toDouble() > 1_000_000 -> UiText.StringResource(R.string.err_amount_too_large)
            state.category.isBlank() -> UiText.StringResource(R.string.err_category_required)
            else -> null
        }
    }
}

data class AddEditExpenseUiState(
    val title: String = "",
    val amount: String = "",
    val category: String = "Other",
    val date: Date = Date(),
    val description: String = "",
    val isIncome: Boolean = false,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
)