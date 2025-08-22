package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.domain.models.Track
import java.io.IOException

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var playerState = 0
    private var currentTrack: Track? = null

    override fun preparePlayer(track: Track) {
        currentTrack = track
        try {
            mediaPlayer.reset()
            track.previewUrl?.let { url ->
                if (url.isNotEmpty()) {
                    mediaPlayer.setDataSource(url)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        playerState = 1
                    }
                    mediaPlayer.setOnCompletionListener {
                        playerState = 1
                        mediaPlayer.seekTo(0)
                    }
                    mediaPlayer.setOnErrorListener { _, _, _ ->
                        playerState = 0
                        false
                    }
                } else {
                    playerState = 0
                }
            } ?: run {
                playerState = 0
            }
        } catch (_: IOException) {
            playerState = 0
        }
    }

    override fun startPlayer() {
        if (playerState == 1) {
            try {
                mediaPlayer.start()
                playerState = 2
            } catch (_: IllegalStateException) {
                playerState = 0
            }
        }
    }

    override fun pausePlayer() {
        if (playerState == 2) {
            mediaPlayer.pause()
            playerState = 1
        }
    }

    override fun stopPlayer() {
        if (playerState != 0) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            playerState = 0
        }
    }

    override fun getCurrentPosition(): Int {
        return if (playerState in 1..3) mediaPlayer.currentPosition else 0
    }

    override fun isPlaying(): Boolean {
        return playerState == 2 && mediaPlayer.isPlaying
    }
}