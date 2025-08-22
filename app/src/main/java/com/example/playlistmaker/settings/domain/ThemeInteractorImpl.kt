package com.example.playlistmaker.settings.domain

class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }
}