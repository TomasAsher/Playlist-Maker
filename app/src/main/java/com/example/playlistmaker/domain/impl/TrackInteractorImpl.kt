package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.impl.Result
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    override suspend fun searchTracks(query: String): Result<List<Track>> =
        repository.searchTracks(query)

    override suspend fun getSearchHistory(): List<Track> = repository.getSearchHistory()
    override suspend fun saveTrackToHistory(track: Track) = repository.saveTrackToHistory(track)
    override suspend fun clearSearchHistory() = repository.clearSearchHistory()
}