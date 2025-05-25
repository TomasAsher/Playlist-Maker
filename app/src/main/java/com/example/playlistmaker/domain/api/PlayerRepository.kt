package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface PlayerRepository {
    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}