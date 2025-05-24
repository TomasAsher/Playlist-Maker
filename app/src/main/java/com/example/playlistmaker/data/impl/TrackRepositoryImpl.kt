package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.impl.Result.Failure
import com.example.playlistmaker.data.impl.Result.Success
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.toDomain
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}

class TrackRepositoryImpl(private val searchHistory: SearchHistory) : TrackRepository {
    override suspend fun searchTracks(query: String): Result<List<Track>> =
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.search(query)
                Success(response.results.mapNotNull { dto ->
                    try {
                        dto.toDomain()
                    } catch (_: Exception) {
                        null
                    }
                })
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun getSearchHistory(): List<Track> = withContext(Dispatchers.IO) {
        searchHistory.getHistory()
    }

    override suspend fun saveTrackToHistory(track: Track) = withContext(Dispatchers.IO) {
        searchHistory.addTrack(track)
    }

    override suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        searchHistory.clearHistory()
    }
}