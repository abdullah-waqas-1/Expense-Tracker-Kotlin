package com.example.expensetracker.data.database

import androidx.room.*
import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, createdAt DESC")
    fun getExpensesBetweenDates(startDate: Date, endDate: Date): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC, createdAt DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 0")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 1")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE isIncome = 0 GROUP BY category")
    fun getExpensesByCategories(): Flow<List<CategorySum>>
}

data class CategorySum(
    val category: String,
    val total: Double
)