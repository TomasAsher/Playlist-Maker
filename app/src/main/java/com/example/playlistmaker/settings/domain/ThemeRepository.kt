package com.example.playlistmaker.settings.domain

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}