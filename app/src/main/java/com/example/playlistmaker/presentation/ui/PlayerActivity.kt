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
        albumCover = findViewById(R.id.albumCover)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        progressTime = findViewById(R.id.progressTime)
        durationTime = findViewById(R.id.durationTime)
        albumName = findViewById(R.id.albumName)
        releaseYear = findViewById(R.id.releaseYear)
        genreLabel = findViewById(R.id.genreLabel)
        countryLabel = findViewById(R.id.countryLabel)
        playPauseButton = findViewById(R.id.playPauseButton)
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_player)
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
            interactor.preparePlayer(track)
            playPauseButton.isEnabled = true
            playPauseButton.setImageResource(R.drawable.ic_play)
        } ?: run {
            playPauseButton.isEnabled = false
        }
    }

    private fun playbackControl() {
        when {
            interactor.isPlaying() -> {
                interactor.pause()
                playPauseButton.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(updateTimeTask ?: return)
            }

            track?.previewUrl != null -> {
                interactor.play()
                playPauseButton.setImageResource(R.drawable.ic_pause)
                startTimer()
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
                    progressTime.text = getString(R.string._0_00)
                    playPauseButton.setImageResource(R.drawable.ic_play)
                    handler.removeCallbacks(this)
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
            handler.removeCallbacks(updateTimeTask ?: return)
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