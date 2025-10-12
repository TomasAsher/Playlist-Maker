package com.example.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        setupThemeSwitch()
        setupClickListeners()
        setupObservers()
    }

    private fun setupThemeSwitch() {
        viewModel.darkThemeEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.themeSwitch.isChecked = enabled
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
        }
    }

    private fun setupClickListeners() {
        binding.buttonShare.setOnClickListener { shareContent() }
        binding.buttonSupport.setOnClickListener { sendSupportEmail() }
        binding.buttonAgreement.setOnClickListener { openAgreementPage() }
    }

    private fun setupObservers() {
        viewModel.loadTheme()
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

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}