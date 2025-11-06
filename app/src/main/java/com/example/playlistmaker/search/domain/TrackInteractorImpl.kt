package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTracks(query: String): Flow<Result<List<Track>>> =
        repository.searchTracks(query)

    override fun getSearchHistory(): Flow<List<Track>> = repository.getSearchHistory()
    override suspend fun saveTrackToHistory(track: Track) = repository.saveTrackToHistory(track)
    override suspend fun clearSearchHistory() = repository.clearSearchHistory()
}