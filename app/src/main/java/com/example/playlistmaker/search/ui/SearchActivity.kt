package com.example.playlistmaker.search.ui

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
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Result
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.HttpException
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var placeholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

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
        historyContainer = findViewById(R.id.history_container)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        val historyAdapter = TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
        historyRecyclerView.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility(editText)
        }
        setupEditText(editText)
        refreshButton.setOnClickListener {
            searchTracks(editText.text.toString())
        }

        viewModel.searchResult.observe(this) { result ->
            progressBar.isVisible = false
            when (result) {
                is Result.Success -> {
                    val tracks = result.data
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
                    adapter.updateTracks(emptyList())
                    if (result.exception is HttpException || result.exception is IOException) {
                        errorPlaceholder.isVisible = true
                        placeholder.isVisible = false
                    } else {
                        placeholder.isVisible = true
                        errorPlaceholder.isVisible = false
                    }
                    recyclerView.isVisible = false
                }
            }
        }

        viewModel.history.observe(this) { history ->
            historyAdapter.updateTracks(history)
            val shouldShowHistory = editText.text.isEmpty() && editText.hasFocus()
            historyContainer.isVisible = shouldShowHistory && history.isNotEmpty()
            recyclerView.isVisible =
                !shouldShowHistory && !placeholder.isVisible && !errorPlaceholder.isVisible
        }
    }

    private fun onTrackClicked(track: Track) {
        viewModel.saveToHistory(track)
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.TRACK_KEY, track)
        }
        startActivity(intent)
    }

    private fun updateHistoryVisibility(editText: EditText) {
        val shouldShowHistory = editText.text.isEmpty() && editText.hasFocus()
        if (shouldShowHistory) {
            viewModel.getHistory()
        } else {
            historyContainer.isVisible = false
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
                    updateHistoryVisibility(editText)
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
                    updateHistoryVisibility(editText)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun updateClearButtonVisibility(editText: EditText, text: CharSequence?) {
        val leftDrawable = editText.compoundDrawablesRelative[0]
        val drawable = if (!text.isNullOrEmpty()) ContextCompat.getDrawable(
            this,
            R.drawable.clear_button_icon
        ) else null
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, drawable, null)
    }

    private fun searchTracks(query: String) {
        progressBar.isVisible = true
        placeholder.isVisible = false
        errorPlaceholder.isVisible = false
        recyclerView.isVisible = false
        viewModel.searchTracks(query)
    }
}