package com.example.playlistmaker.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemePreferences(private val sharedPreferences: SharedPreferences) : ThemeRepository {
    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_DARK_THEME, enabled)
        }
    }
}