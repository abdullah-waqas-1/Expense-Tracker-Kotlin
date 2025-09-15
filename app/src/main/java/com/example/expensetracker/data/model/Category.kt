package com.example.expensetracker.data.model


data class Category(
    val name: String,
    val icon: String,
    val color: Long
)

val expenseCategories = listOf(
    Category("Food", "🍔", 0xFFFF6B6B),
    Category("Transport", "🚗", 0xFF4ECDC4),
    Category("Shopping", "🛍️", 0xFFFFD93D),
    Category("Entertainment", "🎮", 0xFF6BCB77),
    Category("Bills", "📄", 0xFF95E1D3),
    Category("Healthcare", "🏥", 0xFFFF8B94),
    Category("Education", "📚", 0xFFA8E6CF),
    Category("Other", "📌", 0xFFC7CEEA)
)

val incomeCategories = listOf(
    Category("Salary", "💰", 0xFF4CAF50),
    Category("Freelance", "💻", 0xFF2196F3),
    Category("Investment", "📈", 0xFFFFC107),
    Category("Other", "💵", 0xFF9C27B0)
)