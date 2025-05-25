package com.example.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable {
    fun formatTime(): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.Companion.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}