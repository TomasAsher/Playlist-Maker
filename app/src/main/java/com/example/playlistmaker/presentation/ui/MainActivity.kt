package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R

class MainActivity : AppCompatActivity() {
    private lateinit var buttonSearch: Button
    private lateinit var buttonMedia: Button
    private lateinit var buttonSettings: Button
    private val themeInteractor by lazy { Creator.provideThemeInteractor(this) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isSystemDarkTheme =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val isDarkThemeEnabled = themeInteractor.isDarkThemeEnabled()
        if (isSystemDarkTheme != isDarkThemeEnabled) {
            themeInteractor.setDarkThemeEnabled(isSystemDarkTheme)
            AppCompatDelegate.setDefaultNightMode(
                if (isSystemDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        } else {
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        setContentView(R.layout.activity_main)

        buttonSearch = findViewById(R.id.button_search)
        buttonMedia = findViewById(R.id.button_media)
        buttonSettings = findViewById(R.id.button_settings)

        if (!::buttonSearch.isInitialized || !::buttonMedia.isInitialized || !::buttonSettings.isInitialized) {
            return
        }

        buttonSearch.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        buttonMedia.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        buttonSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val isSystemDarkTheme =
            (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val isDarkThemeEnabled = themeInteractor.isDarkThemeEnabled()
        if (isSystemDarkTheme != isDarkThemeEnabled) {
            themeInteractor.setDarkThemeEnabled(isSystemDarkTheme)
            AppCompatDelegate.setDefaultNightMode(
                if (isSystemDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}