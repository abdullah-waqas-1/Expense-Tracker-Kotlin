package com.example.expensetracker.data.repository

import com.example.expensetracker.data.database.ExpenseDao
import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
) {
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    suspend fun deleteAllExpenses() = expenseDao.deleteAllExpenses()
    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)
    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

}