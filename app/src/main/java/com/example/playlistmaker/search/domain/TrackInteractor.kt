package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
    fun getSearchHistory(): Flow<List<Track>>
    suspend fun saveTrackToHistory(track: Track)
    suspend fun clearSearchHistory()
}