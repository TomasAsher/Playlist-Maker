package com.example.playlistmaker.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.domain.ThemeInteractor

class MainViewModelFactory(private val interactor: ThemeInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(interactor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}