package com.example.playlistmaker.search.data

data class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)