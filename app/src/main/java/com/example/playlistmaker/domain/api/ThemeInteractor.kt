package com.example.playlistmaker.domain.api

interface ThemeInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}