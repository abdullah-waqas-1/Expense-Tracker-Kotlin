package com.example.expensetracker.data.model

data class ExpenseStats(
    val totalExpenses: Double,
    val totalIncome: Double,
    val balance: Double,
    val categoryBreakdown: Map<String, Double>
)