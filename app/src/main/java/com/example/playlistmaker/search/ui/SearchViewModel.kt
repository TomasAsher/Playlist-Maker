package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.Result
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class SearchViewModel(private val interactor: TrackInteractor) : ViewModel() {
    private val _searchResult = MutableLiveData<Result<List<Track>>>()
    val searchResult: LiveData<Result<List<Track>>> get() = _searchResult

    private val _history = MutableLiveData<List<Track>>()
    val history: LiveData<List<Track>> get() = _history

    private var lastSearchQuery: String? = null

    fun searchTracks(query: String) {
        lastSearchQuery = query
        viewModelScope.launch {
            _searchResult.value = interactor.searchTracks(query)
        }
    }

    fun getHistory() {
        viewModelScope.launch {
            _history.value = interactor.getSearchHistory()
        }
    }

    fun saveToHistory(track: Track) {
        viewModelScope.launch {
            interactor.saveTrackToHistory(track)
            getHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            interactor.clearSearchHistory()
            getHistory()
        }
    }
}