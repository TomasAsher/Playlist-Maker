package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
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
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.data.impl.Result
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SearchActivity : AppCompatActivity() {
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
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val interactor by lazy { Creator.provideTrackInteractor(applicationContext) }

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: Toolbar = findViewById(R.id.toolbar_search) ?: run {
            println("toolbar_search is null")
            return
        }
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val editText: EditText = findViewById(R.id.editText) ?: run {
            println("editText is null")
            return
        }
        recyclerView = findViewById(R.id.recyclerView) ?: run {
            println("recyclerView is null")
            return
        }
        placeholder = findViewById(R.id.no_results_placeholder) ?: run {
            println("no_results_placeholder is null")
            return
        }
        errorPlaceholder = findViewById(R.id.error_placeholder) ?: run {
            println("error_placeholder is null")
            return
        }
        refreshButton = findViewById(R.id.refresh_button) ?: run {
            println("refresh_button is null")
            return
        }
        historyContainer = findViewById(R.id.history_container) ?: run {
            println("history_container is null")
            return
        }
        historyRecyclerView = findViewById(R.id.history_recycler_view) ?: run {
            println("history_recycler_view is null")
            return
        }
        clearHistoryButton = findViewById(R.id.clear_history_button) ?: run {
            println("clear_history_button is null")
            return
        }
        historyTitle = findViewById(R.id.history_title) ?: run {
            println("history_title is null")
            return
        }
        progressBar = findViewById(R.id.progressBar) ?: run {
            println("progressBar is null")
            return
        }

        println("SearchActivity: Views initialized successfully")

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
        historyRecyclerView.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            println("clearHistoryButton clicked")
            lifecycleScope.launch {
                interactor.clearSearchHistory()
                updateHistoryVisibility()
            }
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            println("editText focus changed: hasFocus=$hasFocus")
            updateHistoryVisibility()
        }
        setupEditText(editText)
        refreshButton.setOnClickListener {
            println("refreshButton clicked")
            searchTracks(editText.text.toString())
        }
    }

    private fun onTrackClicked(track: Track) {
        println("onTrackClicked: $track")
        lifecycleScope.launch {
            interactor.saveTrackToHistory(track)
            updateHistoryVisibility()
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                putExtra(PlayerActivity.TRACK_KEY, track)
            }
            startActivity(intent)
        }
    }

    private fun updateHistoryVisibility() {
        val editText: EditText = findViewById(R.id.editText) ?: run {
            println("editText is null in updateHistoryVisibility")
            return
        }
        val shouldShowHistory = editText.text.isEmpty() && editText.hasFocus()
        lifecycleScope.launch {
            println("updateHistoryVisibility: Fetching history")
            val history = interactor.getSearchHistory()
            println("updateHistoryVisibility: History fetched: $history")
            historyContainer.isVisible = shouldShowHistory && history.isNotEmpty()
            recyclerView.isVisible =
                !shouldShowHistory && !placeholder.isVisible && !errorPlaceholder.isVisible
            if (shouldShowHistory) {
                historyAdapter.updateTracks(history)
                println("updateHistoryVisibility: History updated in adapter")
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(editText, s)
                searchRunnable?.let { handler.removeCallbacks(it) }
                if (s?.isNotEmpty() == true) {
                    searchRunnable = Runnable { searchTracks(s.toString()) }
                    handler.postDelayed(searchRunnable!!, 2000L)
                } else {
                    adapter.updateTracks(emptyList())
                    placeholder.isVisible = false
                    errorPlaceholder.isVisible = false
                    progressBar.isVisible = false
                    updateHistoryVisibility()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = editText.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.x >= (editText.width - editText.paddingEnd - drawableEnd.bounds.width())) {
                    editText.text.clear()
                    adapter.updateTracks(emptyList())
                    placeholder.isVisible = false
                    errorPlaceholder.isVisible = false
                    progressBar.isVisible = false
                    handler.removeCallbacks(searchRunnable ?: return@setOnTouchListener true)
                    updateHistoryVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun updateClearButtonVisibility(editText: EditText, text: CharSequence?) {
        val drawable = if (!text.isNullOrEmpty()) ContextCompat.getDrawable(
            this,
            R.drawable.clear_button_icon
        ) else null
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
    }

    private fun searchTracks(query: String) {
        println("searchTracks called with query: $query")
        lifecycleScope.launch {
            progressBar.isVisible = true
            placeholder.isVisible = false
            errorPlaceholder.isVisible = false
            recyclerView.isVisible = false

            when (val result = interactor.searchTracks(query)) {
                is Result.Success -> {
                    val tracks = result.data
                    println("Search result: $tracks")
                    progressBar.isVisible = false
                    if (tracks.isNotEmpty()) {
                        adapter.updateTracks(tracks)
                        placeholder.isVisible = false
                        errorPlaceholder.isVisible = false
                        recyclerView.isVisible = true
                    } else {
                        adapter.updateTracks(emptyList())
                        placeholder.isVisible = true
                        errorPlaceholder.isVisible = false
                        recyclerView.isVisible = false
                    }
                }

                is Result.Failure -> {
                    println("Search failed with exception: ${result.exception.javaClass.name}, message: ${result.exception.message}")
                    progressBar.isVisible = false
                    adapter.updateTracks(emptyList())
                    if (result.exception is HttpException || result.exception is IOException) {
                        println("Detected network error, showing error_placeholder")
                        errorPlaceholder.isVisible = true
                        placeholder.isVisible = false
                    } else {
                        println("Non-network error, showing no_results_placeholder")
                        placeholder.isVisible = true
                        errorPlaceholder.isVisible = false
                    }
                    recyclerView.isVisible = false
                }
            }
        }
    }
}