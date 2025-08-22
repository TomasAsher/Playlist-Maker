package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.domain.ThemeInteractor

class SettingsViewModelFactory(private val interactor: ThemeInteractor) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(interactor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}