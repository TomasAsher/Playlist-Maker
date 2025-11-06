package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Result
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val searchHistory: SearchHistory) : TrackRepository {
    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = RetrofitClient.apiService.search(query)
            val tracks = response.results.mapNotNull { dto ->
                try {
                    dto.toDomain()
                } catch (_: Exception) {
                    null
                }
            }
            emit(Result.Success(tracks))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun getSearchHistory(): Flow<List<Track>> = flow {
        emit(searchHistory.getHistory())
    }

    override suspend fun saveTrackToHistory(track: Track) {
        searchHistory.addTrack(track)
    }

    override suspend fun clearSearchHistory() {
        searchHistory.clearHistory()
    }
}