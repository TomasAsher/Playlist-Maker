package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"
    private val maxHistorySize = 10

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }
        sharedPreferences.edit {
            putString(key, gson.toJson(history))
        }
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            gson.fromJson(json, Array<Track>::class.java).toList()
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit {
            remove(key)
        }
    }
}