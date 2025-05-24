package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.impl.Result
import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    suspend fun searchTracks(query: String): Result<List<Track>>
    suspend fun getSearchHistory(): List<Track>
    suspend fun saveTrackToHistory(track: Track)
    suspend fun clearSearchHistory()
}