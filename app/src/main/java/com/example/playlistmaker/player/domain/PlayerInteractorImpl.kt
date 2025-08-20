package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(track: Track) {
        repository.preparePlayer(track)
    }

    override fun play() {
        repository.startPlayer()
    }

    override fun pause() {
        repository.pausePlayer()
    }

    override fun stop() {
        repository.stopPlayer()
    }

    override fun getCurrentPosition(): String {
        val currentPosition = repository.getCurrentPosition()
        return SimpleDateFormat("m:ss", Locale.getDefault()).format(currentPosition)
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }
}