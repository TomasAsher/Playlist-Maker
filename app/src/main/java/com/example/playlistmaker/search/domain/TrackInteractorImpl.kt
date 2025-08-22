package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override suspend fun searchTracks(query: String): Result<List<Track>> =
        repository.searchTracks(query)

    override suspend fun getSearchHistory(): List<Track> = repository.getSearchHistory()
    override suspend fun saveTrackToHistory(track: Track) = repository.saveTrackToHistory(track)
    override suspend fun clearSearchHistory() = repository.clearSearchHistory()
}