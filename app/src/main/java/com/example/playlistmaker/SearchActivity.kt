package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyTitle: TextView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar
    private var tracksList: List<Track> = emptyList()
    private lateinit var searchHistory: SearchHistory
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences("app_prefs", MODE_PRIVATE))

        val toolbar: Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val editText: EditText = findViewById(R.id.editText)
        recyclerView = findViewById(R.id.recyclerView)
        placeholder = findViewById(R.id.no_results_placeholder)
        errorPlaceholder = findViewById(R.id.error_placeholder)
        refreshButton = findViewById(R.id.refresh_button)
        historyContainer = findViewById(R.id.history_container)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        historyTitle = findViewById(R.id.history_title)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(tracksList) { track -> onTrackClicked(track) }
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
        historyRecyclerView.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryVisibility()
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility()
        }

        updateHistoryVisibility()

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

    private fun onTrackClicked(track: Track) {
        searchHistory.addTrack(track)
        updateHistoryVisibility()
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.TRACK_KEY, track)
        }
        startActivity(intent)
    }

    private fun updateHistoryVisibility() {
        val history = searchHistory.getHistory()
        val editText: EditText = findViewById(R.id.editText)
        val shouldShowHistory =
            editText.text.isEmpty() && editText.hasFocus() && history.isNotEmpty()

        historyContainer.isVisible = shouldShowHistory
        recyclerView.isVisible =
            !shouldShowHistory && !placeholder.isVisible && !errorPlaceholder.isVisible

        if (shouldShowHistory) {
            historyAdapter.updateTracks(history)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEditText(editText: EditText) {
        editText.imeOptions = EditorInfo.IME_ACTION_NONE

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(editText, s)
                searchText = s?.toString() ?: ""
                updateHistoryVisibility()

                searchRunnable?.let { handler.removeCallbacks(it) }
                if (searchText.isNotEmpty()) {
                    searchRunnable = Runnable { searchTracks(searchText) }
                    handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
                } else {
                    tracksList = emptyList()
                    adapter.updateTracks(emptyList())
                    placeholder.isVisible = false
                    errorPlaceholder.isVisible = false
                    progressBar.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = editText.compoundDrawablesRelative[2]
                if (drawableEnd != null && isClearButtonClicked(editText, event, drawableEnd)) {
                    editText.text.clear()
                    searchText = ""
                    tracksList = emptyList()
                    adapter.updateTracks(emptyList())
                    placeholder.isVisible = false
                    errorPlaceholder.isVisible = false
                    progressBar.isVisible = false
                    handler.removeCallbacks(searchRunnable ?: return@setOnTouchListener true)
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun updateClearButtonVisibility(editText: EditText, text: CharSequence?) {
        val drawable = if (!text.isNullOrEmpty()) {
            ContextCompat.getDrawable(this, R.drawable.clear_button_icon)
        } else {
            null
        }
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            editText.compoundDrawablesRelative[0],
            null,
            drawable,
            null
        )
    }

    private fun isClearButtonClicked(
        editText: EditText,
        event: MotionEvent,
        drawableEnd: Drawable
    ): Boolean {
        return event.x >= (editText.width - editText.paddingEnd - drawableEnd.bounds.width())
    }

    private fun searchTracks(query: String) {
        lifecycleScope.launch {
            try {
                progressBar.isVisible = true
                placeholder.isVisible = false
                errorPlaceholder.isVisible = false
                recyclerView.isVisible = false

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.search(query)
                }

                progressBar.isVisible = false
                if (response.results.isNotEmpty()) {
                    tracksList = response.results
                    adapter.updateTracks(tracksList)
                    placeholder.isVisible = false
                    errorPlaceholder.isVisible = false
                    recyclerView.isVisible = true
                } else {
                    tracksList = emptyList()
                    adapter.updateTracks(emptyList())
                    placeholder.isVisible = true
                    errorPlaceholder.isVisible = false
                    recyclerView.isVisible = false
                }
            } catch (_: Exception) {
                progressBar.isVisible = false
                tracksList = emptyList()
                adapter.updateTracks(emptyList())
                errorPlaceholder.isVisible = true
                placeholder.isVisible = false
                recyclerView.isVisible = false
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

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
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