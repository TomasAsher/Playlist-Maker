package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var placeholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var refreshButton: Button
    private var tracksList: List<Track> = emptyList()

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val editText: EditText = findViewById(R.id.editText)
        recyclerView = findViewById(R.id.recyclerView)
        placeholder = findViewById(R.id.no_results_placeholder)
        errorPlaceholder = findViewById(R.id.error_placeholder)
        refreshButton = findViewById(R.id.refresh_button)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(tracksList)
        recyclerView.adapter = adapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText") ?: ""
            tracksList =
                savedInstanceState.getParcelableArrayListCompat<Track>("tracks") ?: emptyList()
            val isPlaceholderVisible = savedInstanceState.getBoolean("placeholderVisible", false)
            val isErrorVisible = savedInstanceState.getBoolean("errorVisible", false)

            editText.setText(searchText)
            adapter.updateTracks(tracksList)
            placeholder.isVisible = isPlaceholderVisible
            errorPlaceholder.isVisible = isErrorVisible
            recyclerView.isVisible = !isPlaceholderVisible && !isErrorVisible
        }

        setupEditText(editText)

        refreshButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                searchTracks(searchText)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        editText.compoundDrawablesRelative[0], null, null, null
                    )
                    placeholder.isVisible = false
                    errorPlaceholder.isVisible = false
                } else {
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        editText.compoundDrawablesRelative[0], null,
                        resources.getDrawable(R.drawable.clear_button_icon, theme), null
                    )
                }
                searchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = editText.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val isClearButtonClicked =
                        event.x >= (editText.width - editText.paddingEnd - drawableEnd.bounds.width())
                    if (isClearButtonClicked) {
                        editText.text.clear()
                        searchText = ""
                        tracksList = emptyList()
                        adapter.updateTracks(emptyList())
                        placeholder.isVisible = false
                        errorPlaceholder.isVisible = false
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchText.isNotEmpty()) {
                    searchTracks(searchText)
                }
                true
            }
            false
        }
    }

    private fun searchTracks(query: String) {
        lifecycleScope.launch {
            try {
                placeholder.isVisible = false
                errorPlaceholder.isVisible = false
                recyclerView.isVisible = true

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.search(query)
                }

                if (response.results.isNotEmpty()) {
                    tracksList = response.results
                    adapter.updateTracks(tracksList)
                    placeholder.isVisible = false
                    errorPlaceholder.isVisible = false
                } else {
                    tracksList = emptyList()
                    adapter.updateTracks(emptyList())
                    placeholder.isVisible = true
                    errorPlaceholder.isVisible = false
                }
            } catch (_: Exception) {
                tracksList = emptyList()
                adapter.updateTracks(emptyList())
                errorPlaceholder.isVisible = true
                placeholder.isVisible = false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchText", searchText)
        outState.putParcelableArrayList("tracks", ArrayList(tracksList))
        outState.putBoolean("placeholderVisible", placeholder.isVisible)
        outState.putBoolean("errorVisible", errorPlaceholder.isVisible)
    }

    @SuppressLint("NewApi")
    inline fun <reified T : Parcelable> Bundle.getParcelableArrayListCompat(key: String): List<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayList(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableArrayList(key)
        }
    }
}