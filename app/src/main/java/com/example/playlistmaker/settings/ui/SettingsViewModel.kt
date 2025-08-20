package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeInteractor

class SettingsViewModel(private val interactor: ThemeInteractor) : ViewModel() {
    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> get() = _darkThemeEnabled

    fun loadTheme() {
        _darkThemeEnabled.value = interactor.isDarkThemeEnabled()
    }

    fun setDarkTheme(enabled: Boolean) {
        interactor.setDarkThemeEnabled(enabled)
        _darkThemeEnabled.value = enabled
    }
}