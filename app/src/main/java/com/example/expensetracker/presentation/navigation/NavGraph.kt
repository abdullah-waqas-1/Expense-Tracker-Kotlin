package com.example.expensetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.presentation.ui.screen.StatsScreen
import com.example.expensetrackertest.presentation.ui.screen.AddEditExpenseScreen
import com.example.expensetrackertest.presentation.ui.screen.ExpenseListScreen

@Composable
fun ExpenseTrackerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "expense_list"
    ) {
        composable("expense_list") {
            ExpenseListScreen(
                onNavigateToAddExpense = {
                    navController.navigate("add_expense")
                },
                onNavigateToEditExpense = { expenseId ->
                    navController.navigate("edit_expense/$expenseId")
                },
                onNavigateToStats = {
                    navController.navigate("stats")
                }
            )
        }

        composable("add_expense") {
            AddEditExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("edit_expense/{expenseId}") {
            AddEditExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("stats") {
            StatsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}