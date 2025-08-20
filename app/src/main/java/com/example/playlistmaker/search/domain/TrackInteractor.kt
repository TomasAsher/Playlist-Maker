package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractor {
    suspend fun searchTracks(query: String): Result<List<Track>>
    suspend fun getSearchHistory(): List<Track>
    suspend fun saveTrackToHistory(track: Track)
    suspend fun clearSearchHistory()
}