package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track

fun TrackDto.toDomain(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}