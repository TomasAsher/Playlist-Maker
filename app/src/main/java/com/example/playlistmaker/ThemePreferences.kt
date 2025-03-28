package com.example.playlistmaker

import android.content.Context
import androidx.core.content.edit

object ThemePreferences {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_THEME = "dark_theme"

    fun isDarkThemeEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_THEME, false)
    }

    fun setDarkThemeEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit() {
                putBoolean(KEY_DARK_THEME, enabled)
            }
    }
}