package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    private val key = "search_history"
    private val maxHistorySize = 10

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }
        try {
            sharedPreferences.edit {
                putString(key, gson.toJson(history))
            }
        } catch (_: Exception) {
        }
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            try {
                gson.fromJson(json, Array<Track>::class.java)?.toList() ?: emptyList()
            } catch (_: JsonSyntaxException) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        try {
            sharedPreferences.edit {
                remove(key)
            }
        } catch (_: Exception) {
        }
    }
}