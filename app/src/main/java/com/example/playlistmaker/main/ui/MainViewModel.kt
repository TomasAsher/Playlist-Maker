package com.example.playlistmaker.main.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeInteractor

class MainViewModel(private val interactor: ThemeInteractor) : ViewModel() {
    fun syncWithSystem(isSystemDark: Boolean) {
        val current = interactor.isDarkThemeEnabled()
        if (current != isSystemDark) {
            interactor.setDarkThemeEnabled(isSystemDark)
            AppCompatDelegate.setDefaultNightMode(
                if (isSystemDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}