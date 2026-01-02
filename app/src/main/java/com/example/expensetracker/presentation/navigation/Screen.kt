package com.example.expensetracker.presentation.navigation

sealed class Screen(val route: String) {
    data object ExpenseList : Screen("expense_list")
    data object AddExpense : Screen("add_expense")
    data object Stats : Screen("stats")

    data object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: Long) = "edit_expense/$expenseId"
    }
}