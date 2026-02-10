package com.example.expensetracker.data.model

data class Category(
    val name: String,
    val icon: String,
    val color: Long
)

val expenseCategories = listOf(
    Category("Food", "\uD83C\uDF54", 0xFFFF6B6B),
    Category("Transport", "\uD83C\uDFCD\uFE0F", 0xFF4ECDC4),
    Category("Shopping", "\uD83D\uDECD\uFE0F", 0xFFFFD93D),
    Category("Grocery", "\uD83D\uDED2", 0xFF6BCB77),
    Category("Bills", "\uD83D\uDCDD", 0xFF95E1D3),
    Category("Healthcare", "\uD83C\uDFE5", 0xFFFF8B94),
    Category("Home", "\uD83C\uDFE0", 0xFFA8E6CF),
    Category("Other", "\uD83D\uDCCC", 0xFFC7CEEA)
)

val incomeCategories = listOf(
    Category("Salary", "\uD83D\uDCB0", 0xFF4CAF50),
    Category("Freelance", "\uD83D\uDCBB", 0xFF2196F3),
    Category("Investment", "\uD83D\uDCC8", 0xFFFFC107),
    Category("Other", "\uD83D\uDCB8", 0xFF9C27B0)
)