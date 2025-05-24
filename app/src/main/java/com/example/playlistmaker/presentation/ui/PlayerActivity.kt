package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_KEY = "track"
    }

    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var progressTime: TextView
    private lateinit var durationTime: TextView
    private lateinit var albumName: TextView
    private lateinit var releaseYear: TextView
    private lateinit var genreLabel: TextView
    private lateinit var countryLabel: TextView
    private lateinit var playPauseButton: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimeTask: Runnable? = null
    private var track: Track? = null
    private val interactor by lazy { Creator.providePlayerInteractor() }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupToolbar()
        handleIntent()

        playPauseButton.setOnClickListener { playbackControl() }
    }

    private fun initViews() {
        albumCover = findViewById(R.id.albumCover) ?: run {
            println("albumCover is null")
            return
        }
        trackName = findViewById(R.id.trackName) ?: run {
            println("trackName is null")
            return
        }
        artistName = findViewById(R.id.artistName) ?: run {
            println("artistName is null")
            return
        }
        progressTime = findViewById(R.id.progressTime) ?: run {
            println("progressTime is null")
            return
        }
        durationTime = findViewById(R.id.durationTime) ?: run {
            println("durationTime is null")
            return
        }
        albumName = findViewById(R.id.albumName) ?: run {
            println("albumName is null")
            return
        }
        releaseYear = findViewById(R.id.releaseYear) ?: run {
            println("releaseYear is null")
            return
        }
        genreLabel = findViewById(R.id.genreLabel) ?: run {
            println("genreLabel is null")
            return
        }
        countryLabel = findViewById(R.id.countryLabel) ?: run {
            println("countryLabel is null")
            return
        }
        playPauseButton = findViewById(R.id.playPauseButton) ?: run {
            println("playPauseButton is null")
            return
        }
        println("PlayerActivity: Views initialized successfully")
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_player) ?: run {
            println("toolbar_player is null")
            return
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handleIntent() {
        track = intent.getParcelableExtra(TRACK_KEY, Track::class.java)
        track?.let { setupPlayer(it) }
    }

    private fun setupPlayer(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.track_placeholder)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen._8dp)))
            .into(albumCover)

        trackName.text = track.trackName
        artistName.text = track.artistName
        durationTime.text = track.formatTime()
        progressTime.text = getString(R.string._0_00)

        track.collectionName?.let { albumName.text = it }
        track.releaseDate?.let { releaseYear.text = it.take(4) }
        track.primaryGenreName?.let { genreLabel.text = it }
        track.country?.let { countryLabel.text = it }

        track.previewUrl?.let {
            println("Setting up player with previewUrl: $it")
            interactor.preparePlayer(track)
            playPauseButton.isEnabled = true
            playPauseButton.setImageResource(R.drawable.ic_play)
        } ?: run {
            println("No previewUrl, disabling playPauseButton")
            playPauseButton.isEnabled = false
        }
    }

    private fun playbackControl() {
        println("playbackControl called, isPlaying = ${interactor.isPlaying()}")
        when {
            interactor.isPlaying() -> {
                println("Pausing player")
                interactor.pause()
                playPauseButton.setImageResource(R.drawable.ic_play)
            }

            track?.previewUrl != null -> {
                println("Starting player")
                interactor.play()
                playPauseButton.setImageResource(R.drawable.ic_pause)
                startTimer()
            }

            else -> {
                println("Cannot play, no previewUrl or player not prepared")
            }
        }
    }

    private fun startTimer() {
        updateTimeTask = object : Runnable {
            override fun run() {
                if (interactor.isPlaying()) {
                    progressTime.text = interactor.getCurrentPosition()
                    handler.postDelayed(this, 300L)
                } else {
                    playPauseButton.setImageResource(R.drawable.ic_play)
                }
            }
        }
        handler.post(updateTimeTask ?: return)
    }

    override fun onPause() {
        super.onPause()
        if (interactor.isPlaying()) {
            interactor.pause()
            playPauseButton.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeTask ?: return)
        interactor.stop()
        playPauseButton.setImageResource(R.drawable.ic_play)
    }

    @Deprecated("Deprecated in favor of OnBackPressedDispatcher")
    override fun onBackPressed() {
        if (interactor.isPlaying()) {
            interactor.stop()
            playPauseButton.setImageResource(R.drawable.ic_play)
        }
        super.onBackPressed()
    }
}