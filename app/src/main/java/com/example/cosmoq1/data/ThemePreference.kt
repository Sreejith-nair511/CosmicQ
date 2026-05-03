package com.example.cosmoq1.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cosmoq1.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

object ThemePreference {
    private val THEME_KEY = stringPreferencesKey("selected_theme")

    fun getTheme(context: Context): Flow<AppTheme> =
        context.themeDataStore.data.map { prefs ->
            when (prefs[THEME_KEY]) {
                AppTheme.GALAXY_PURPLE.name -> AppTheme.GALAXY_PURPLE
                AppTheme.SOLAR_GOLD.name    -> AppTheme.SOLAR_GOLD
                else                        -> AppTheme.DEEP_SPACE
            }
        }

    suspend fun saveTheme(context: Context, theme: AppTheme) {
        context.themeDataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}
