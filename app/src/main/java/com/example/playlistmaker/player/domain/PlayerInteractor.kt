package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.models.Track

interface PlayerInteractor {
    fun preparePlayer(track: Track)
    fun play()
    fun pause()
    fun stop()
    fun getCurrentPosition(): String
    fun isPlaying(): Boolean
}