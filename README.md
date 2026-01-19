# 💰 Expense Tracker - Android

A modern, "Lively" personal finance application built with **Jetpack Compose** and **Kotlin**. This app helps users track their daily income and expenses with a clean, intuitive UI, offering insightful statistics and dynamic filtering.

## 📱 Screenshots

| **Transaction List** | **Add Transaction** | **Spending Analysis** |
|:--------------------:|:-------------------:|:---------------------:|
| *![1000332814](https://github.com/user-attachments/assets/7d83db23-d606-41ab-b670-3fcd47596d4a)* | *![1000332813](https://github.com/user-attachments/assets/c8454907-359c-463e-8cde-93a8fe9dcf67)* | *![1000332812](https://github.com/user-attachments/assets/530d1b1d-a611-4f83-bf56-0d573c21f57e)* |
| View your balance, income, and expense history with smart filters. | Add or edit entries with category selection and date picker. | Visual breakdown of expenses using an animated donut chart. |

---

## ✨ Features

* **💸 Transaction Management:** Easily add, edit, and delete income and expense transactions.
* **📊 Dynamic Dashboard:**
    * **Balance Card:** Real-time calculation of Total Balance, Total Income, and Total Expense.
    * **Smart Filtering:** Filter transactions by specific Category or Date.
    * **Context-Aware Stats:** The Balance Card dynamically updates to show totals for *only* the filtered category (e.g., see exactly how much you spent on "Food").
* **📈 Spending Analysis:**
    * Visual **Donut Chart** with animation.
    * Detailed category breakdown sorted by highest spending.
    * "Growth" indicators for income and expenses.
* **📅 Smart Sorting:** Transactions are sorted by Date, with the most recently added items appearing at the top (even for the same day).
* **🎨 "Lively" Design System:**
    * Custom Purple & White theme (`#6200EE`).
    * Squircle iconography and gradient cards.
    * Smooth transitions and Material 3 animations.
* **💾 Offline First:** All data is persisted locally using **Room Database**, ensuring privacy and offline access.

---

## 🛠 Tech Stack & Libraries

The project follows modern Android development practices:

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture
* **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
* **Database:** [Room](https://developer.android.com/training/data-storage/room) (SQLite)
* **Asynchronous Programming:** Kotlin Coroutines & Flow
* **Navigation:** Navigation Compose
* **Vector Graphics:** Custom XML Vectors for Icons and Splash screens.

---

## 📂 Project Structure

The app is modularized by layer (Clean Architecture):

```text
com.example.expensetracker
├── data                # Data Layer
│   ├── database        # Room DAO, Entities, Converters
│   ├── model           # Data classes (Expense, Category, Stats)
│   ├── repository      # Repository implementation
│   └── network         # (Prepared for future API integration)
├── di                  # Hilt Dependency Injection Modules
├── presentation        # UI Layer
│   ├── navigation      # NavGraph and Screen Routes
│   ├── ui
│   │   ├── component   # Reusable UI parts (DonutChart, ExpenseItem, etc.)
│   │   ├── screen      # Main Screens (List, Add/Edit, Stats)
│   │   └── theme       # Color, Type, and Theme definitions
│   └── viewmodel       # ViewModels managing UI state
└── ExpenseTrackerApplication.kt
