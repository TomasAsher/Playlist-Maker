package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.models.Track

interface PlayerRepository {
    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}