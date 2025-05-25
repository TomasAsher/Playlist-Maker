package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    private val themeInteractor by lazy { Creator.provideThemeInteractor(this) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitch = findViewById<SwitchCompat>(R.id.theme_switch)
        val isSystemDarkTheme =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val isDarkThemeEnabled = themeInteractor.isDarkThemeEnabled()
        themeSwitch.isChecked = isSystemDarkTheme || isDarkThemeEnabled
        if (isSystemDarkTheme != isDarkThemeEnabled) {
            themeInteractor.setDarkThemeEnabled(isSystemDarkTheme)
            AppCompatDelegate.setDefaultNightMode(
                if (isSystemDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            themeInteractor.setDarkThemeEnabled(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        val buttonShare: TextView = findViewById(R.id.buttonShare)
        buttonShare.setOnClickListener {
            shareContent()
        }

        val buttonSupport: TextView = findViewById(R.id.buttonSupport)
        buttonSupport.setOnClickListener {
            sendSupportEmail()
        }

        val buttonAgreement: TextView = findViewById(R.id.buttonAgreement)
        buttonAgreement.setOnClickListener {
            openAgreementPage()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar_settings)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val isSystemDarkTheme =
            (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val themeSwitch = findViewById<SwitchCompat>(R.id.theme_switch)
        if (themeSwitch.isChecked != isSystemDarkTheme) {
            themeSwitch.isChecked = isSystemDarkTheme
            themeInteractor.setDarkThemeEnabled(isSystemDarkTheme)
            AppCompatDelegate.setDefaultNightMode(
                if (isSystemDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun openAgreementPage() {
        val url = getString(R.string.agreement_url)
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendSupportEmail() {
        val email = "thetimurik@yandex.ru"
        val subject = getString(R.string.messageTheme)
        val body = getString(R.string.messageMail)
        val toast = getString(R.string.toastMail)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareContent() {
        val shareText = getString(R.string.practicum_android)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }
}