package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.data.local.ThemePreferences
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.presentation.navigation.ExpenseTrackerNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themePreferences: ThemePreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ExpenseTracker)
        super.onCreate(savedInstanceState)
        setContent {
            val savedThemeByPlayer by themePreferences.themeMode.collectAsState(initial = null)
            val systemTheme = isSystemInDarkTheme()
            val isDarkTheme = savedThemeByPlayer ?: systemTheme

            ExpenseTrackerTheme(
                darkTheme = isDarkTheme,
                onToggle = {
                    lifecycleScope.launch {
                        themePreferences.saveTheme(!isDarkTheme)
                    }
                }
            ) {
                ExpenseTrackerNavGraph()
            }
        }
    }
}