package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface PlayerInteractor {
    fun preparePlayer(track: Track)
    fun play()
    fun pause()
    fun stop()
    fun getCurrentPosition(): String
    fun isPlaying(): Boolean
}