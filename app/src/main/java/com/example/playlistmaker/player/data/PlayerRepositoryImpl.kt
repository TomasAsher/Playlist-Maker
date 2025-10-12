package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.domain.models.Track
import java.io.IOException

class PlayerRepositoryImpl : PlayerRepository {
    private val mediaPlayer = MediaPlayer()
    private var isPrepared = false

    override fun preparePlayer(track: Track) {
        try {
            mediaPlayer.reset()
            track.previewUrl?.let { url ->
                if (url.isNotEmpty()) {
                    mediaPlayer.setDataSource(url)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        isPrepared = true
                    }
                    mediaPlayer.setOnCompletionListener {
                        isPrepared = true
                        mediaPlayer.seekTo(0)
                    }
                    mediaPlayer.setOnErrorListener { _, _, _ ->
                        isPrepared = false
                        false
                    }
                } else {
                    isPrepared = false
                }
            } ?: run {
                isPrepared = false
            }
        } catch (_: IOException) {
            isPrepared = false
        }
    }

    override fun startPlayer() {
        if (isPrepared) {
            try {
                mediaPlayer.start()
            } catch (_: IllegalStateException) {
                isPrepared = false
            }
        }
    }

    override fun pausePlayer() {
        if (isPrepared && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun stopPlayer() {
        if (isPrepared) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            isPrepared = false
        }
    }

    override fun getCurrentPosition(): Int {
        return if (isPrepared) mediaPlayer.currentPosition else 0
    }

    override fun isPlaying(): Boolean {
        return isPrepared && mediaPlayer.isPlaying
    }
}