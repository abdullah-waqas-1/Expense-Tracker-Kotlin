package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Date,
    val description: String? = null,
    val isIncome: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)