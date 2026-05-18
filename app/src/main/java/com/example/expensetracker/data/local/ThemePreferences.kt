package com.example.expensetracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class ThemePreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val THEME_KEY = booleanPreferencesKey("is_dark_theme")
    private val BALANCE_VISIBILITY_KEY = booleanPreferencesKey("is_balance_visible")

    val themeMode: Flow<Boolean?> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY]
        }

    val isBalanceVisible: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[BALANCE_VISIBILITY_KEY] ?: true
        }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }

    suspend fun saveBalanceVisibility(isVisible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BALANCE_VISIBILITY_KEY] = isVisible
        }
    }
}