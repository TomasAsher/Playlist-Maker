package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Result
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(private val searchHistory: SearchHistory) : TrackRepository {
    override suspend fun searchTracks(query: String): Result<List<Track>> =
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.search(query)
                Result.Success(response.results.mapNotNull { dto ->
                    try {
                        dto.toDomain()
                    } catch (_: Exception) {
                        null
                    }
                })
            } catch (e: Exception) {
                Result.Failure(e)
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