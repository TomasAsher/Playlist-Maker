package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.Result
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.HttpException
import java.io.IOException

class SearchFragment : Fragment(R.layout.fragment_search) {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        setupRecyclerView()
        setupHistoryRecyclerView()
        setupSearchEditText()
        setupObservers()
        setupRefreshButton()
        setupClearHistoryButton()
        viewModel.getHistory()
    }

    private fun setupRecyclerView() {
        searchAdapter = TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = searchAdapter
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = TrackAdapter(emptyList()) { track -> onTrackClicked(track) }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.isVisible = false
            when (result) {
                is Result.Success -> {
                    val tracks = result.data
                    if (tracks.isNotEmpty()) {
                        showSearchResults(tracks)
                    } else {
                        showNoResults()
                    }
                }

                is Result.Failure -> {
                    searchAdapter.updateTracks(emptyList())
                    if (result.exception is HttpException || result.exception is IOException) {
                        showError()
                    } else {
                        showNoResults()
                    }
                }
            }
        }

        viewModel.history.observe(viewLifecycleOwner) { history ->
            if (history.isNotEmpty()) {
                historyAdapter.updateTracks(history)
                showHistory()
            } else {
                hideHistory()
            }
        }
    }

    private fun showSearchResults(tracks: List<Track>) {
        searchAdapter.updateTracks(tracks)
        binding.recyclerView.isVisible = true
        binding.historyContainer.isVisible = false
        binding.noResultsPlaceholder.isVisible = false
        binding.errorPlaceholder.isVisible = false
    }

    private fun showNoResults() {
        searchAdapter.updateTracks(emptyList())
        binding.noResultsPlaceholder.isVisible = true
        binding.errorPlaceholder.isVisible = false
        binding.recyclerView.isVisible = false
        binding.historyContainer.isVisible = false
    }

    private fun showError() {
        searchAdapter.updateTracks(emptyList())
        binding.errorPlaceholder.isVisible = true
        binding.noResultsPlaceholder.isVisible = false
        binding.recyclerView.isVisible = false
        binding.historyContainer.isVisible = false
    }

    private fun showHistory() {
        binding.historyContainer.isVisible = true
        binding.recyclerView.isVisible = false
        binding.noResultsPlaceholder.isVisible = false
        binding.errorPlaceholder.isVisible = false
    }

    private fun hideHistory() {
        binding.historyContainer.isVisible = false
    }

    private fun onTrackClicked(track: Track) {
        viewModel.saveToHistory(track)
        val bundle = bundleOf("track" to track)
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            bundle
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchEditText() {
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateClearButtonVisibility(s)
                searchJob?.cancel()

                if (s?.isNotEmpty() == true) {
                    searchJob = kotlinx.coroutines.MainScope().launch {
                        delay(2000)
                        showProgress()
                        viewModel.searchTracks(s.toString())
                    }
                } else {
                    searchAdapter.updateTracks(emptyList())
                    binding.noResultsPlaceholder.isVisible = false
                    binding.errorPlaceholder.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.recyclerView.isVisible = false
                    viewModel.getHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.editText.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.x >= (binding.editText.width - binding.editText.paddingEnd - drawableEnd.bounds.width())) {
                    binding.editText.text.clear()
                    searchAdapter.updateTracks(emptyList())
                    binding.noResultsPlaceholder.isVisible = false
                    binding.errorPlaceholder.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.recyclerView.isVisible = false
                    viewModel.getHistory()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setupRefreshButton() {
        binding.refreshButton.setOnClickListener {
            val query = binding.editText.text.toString()
            if (query.isNotEmpty()) {
                showProgress()
                viewModel.searchTracks(query)
            }
        }
    }

    private fun setupClearHistoryButton() {
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.noResultsPlaceholder.isVisible = false
        binding.errorPlaceholder.isVisible = false
        binding.historyContainer.isVisible = false
    }

    private fun updateClearButtonVisibility(text: CharSequence?) {
        val leftDrawable = binding.editText.compoundDrawablesRelative[0]
        val drawable = if (!text.isNullOrEmpty()) ContextCompat.getDrawable(
            requireContext(),
            R.drawable.clear_button_icon
        ) else null
        binding.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            leftDrawable,
            null,
            drawable,
            null
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}