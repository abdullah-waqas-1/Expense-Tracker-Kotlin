package com.example.expensetracker.data.repository


import com.example.expensetracker.data.database.ExpenseDao
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.ExpenseStats
import com.example.expensetracker.data.network.CurrencyApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val currencyApi: CurrencyApi
) {
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesBetweenDates(startDate: Date, endDate: Date): Flow<List<Expense>> =
        expenseDao.getExpensesBetweenDates(startDate, endDate)

    fun getExpensesByCategory(category: String): Flow<List<Expense>> =
        expenseDao.getExpensesByCategory(category)

    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    fun getExpenseStats(): Flow<ExpenseStats> = combine(
        expenseDao.getTotalExpenses(),
        expenseDao.getTotalIncome(),
        expenseDao.getExpensesByCategories()
    ) { totalExpenses, totalIncome, categoryBreakdown ->
        ExpenseStats(
            totalExpenses = totalExpenses ?: 0.0,
            totalIncome = totalIncome ?: 0.0,
            balance = (totalIncome ?: 0.0) - (totalExpenses ?: 0.0),
            categoryBreakdown = categoryBreakdown.associate { it.category to it.total }
        )
    }

    suspend fun getExchangeRates(base: String) = try {
        currencyApi.getExchangeRates(base)
    } catch (e: Exception) {
        null
    }
}