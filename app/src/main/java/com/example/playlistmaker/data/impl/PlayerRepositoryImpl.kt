package com.example.playlistmaker.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.models.Track
import java.io.IOException

class PlayerRepositoryImpl : PlayerRepository {
    private val mediaPlayer = MediaPlayer()
    private var playerState = 0
    private var currentTrack: Track? = null

    override fun preparePlayer(track: Track) {
        currentTrack = track
        try {
            mediaPlayer.reset()
            track.previewUrl?.let { url ->
                if (url.isNotEmpty()) {
                    println("Preparing player with URL: $url")
                    mediaPlayer.setDataSource(url)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        println("Player prepared successfully, playerState = 1")
                        playerState = 1
                    }
                    mediaPlayer.setOnCompletionListener {
                        println("Playback completed, playerState = 1")
                        playerState = 1
                        mediaPlayer.seekTo(0)
                    }
                    mediaPlayer.setOnErrorListener { _, what, extra ->
                        println("Player error: what=$what, extra=$extra, playerState = 0")
                        playerState = 0
                        false
                    }
                } else {
                    println("Empty previewUrl, playerState = 0")
                    playerState = 0
                }
            } ?: run {
                println("No previewUrl provided, playerState = 0")
                playerState = 0
            }
        } catch (e: IOException) {
            println("IOException during preparePlayer: ${e.message}, playerState = 0")
            playerState = 0
        }
    }

    override fun startPlayer() {
        println("startPlayer called, current playerState = $playerState")
        if (playerState == 1) {
            try {
                mediaPlayer.start()
                playerState = 2
                println("Player started, new playerState = 2")
            } catch (e: IllegalStateException) {
                println("IllegalStateException during start: ${e.message}, playerState = 0")
                playerState = 0
            }
        } else {
            println("Cannot start player, invalid state: $playerState")
        }
    }

    override fun pausePlayer() {
        println("pausePlayer called, current playerState = $playerState")
        if (playerState == 2) {
            mediaPlayer.pause()
            playerState = 1
            println("Player paused, new playerState = 1")
        } else {
            println("Cannot pause player, invalid state: $playerState")
        }
    }

    override fun stopPlayer() {
        println("stopPlayer called, current playerState = $playerState")
        if (playerState != 0) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            playerState = 0
            println("Player stopped and reset, new playerState = 0")
        }
    }

    override fun getCurrentPosition(): Int {
        return if (playerState in 1..3) mediaPlayer.currentPosition else 0
    }

    override fun isPlaying(): Boolean {
        println("isPlaying called, playerState = $playerState, mediaPlayer.isPlaying = ${mediaPlayer.isPlaying}")
        return playerState == 2 && mediaPlayer.isPlaying
    }
}