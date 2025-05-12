package com.example.playlistmaker

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_INTERVAL = 300L
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

    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimeTask: Runnable? = null
    private var track: Track? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupToolbar()
        handleIntent()

        playPauseButton.setOnClickListener {
            playbackControl()
        }
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

    private fun handleIntent() {
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_KEY)
        }
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
            preparePlayer(it)
        } ?: run {
            playPauseButton.isEnabled = false
        }
    }

    private fun preparePlayer(url: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playPauseButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playPauseButton.setImageResource(R.drawable.ic_play)
                playerState = STATE_PREPARED
                progressTime.text = getString(R.string._0_00)
                handler.removeCallbacks(updateTimeTask ?: return@setOnCompletionListener)
            }
            mediaPlayer.setOnErrorListener { _, what, extra ->
                playPauseButton.isEnabled = false
                playerState = STATE_DEFAULT
                progressTime.text = getString(R.string._0_00)
                true
            }
        } catch (_: Exception) {
            playPauseButton.isEnabled = false
            playerState = STATE_DEFAULT
        }
    }

    private fun startPlayer() {
        try {
            mediaPlayer.start()
            playPauseButton.setImageResource(R.drawable.ic_pause)
            playerState = STATE_PLAYING
            startTimer()
        } catch (_: Exception) {
            playPauseButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_DEFAULT
            playPauseButton.isEnabled = false
        }
    }

    private fun pausePlayer() {
        try {
            mediaPlayer.pause()
            playPauseButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_PAUSED
            handler.removeCallbacks(updateTimeTask ?: return)
        } catch (_: Exception) {
            playPauseButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_DEFAULT
            playPauseButton.isEnabled = false
        }
    }

    private fun stopPlayer() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            playPauseButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_DEFAULT
            progressTime.text = getString(R.string._0_00)
            handler.removeCallbacks(updateTimeTask ?: return)
        } catch (_: Exception) {
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
            STATE_DEFAULT -> {
                track?.previewUrl?.let {
                    preparePlayer(it)
                    playPauseButton.isEnabled = false
                } ?: run {
                    playPauseButton.isEnabled = false
                }
            }
        }
    }

    private fun startTimer() {
        updateTimeTask = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val currentPosition = mediaPlayer.currentPosition
                    progressTime.text = SimpleDateFormat("m:ss", Locale.getDefault())
                        .format(currentPosition)
                    handler.postDelayed(this, UPDATE_INTERVAL)
                }
            }
        }
        handler.post(updateTimeTask ?: return)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeTask ?: return)
        mediaPlayer.release()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (playerState == STATE_PLAYING || playerState == STATE_PAUSED) {
            stopPlayer()
        }
        super.onBackPressed()
    }
}