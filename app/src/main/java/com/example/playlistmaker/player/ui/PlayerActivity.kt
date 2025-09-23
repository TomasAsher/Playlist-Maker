package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val TRACK_KEY = "track"
    }

    private val viewModel: PlayerViewModel by viewModel()
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
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupToolbar()
        handleIntent()

        playPauseButton.setOnClickListener { viewModel.playbackControl() }

        viewModel.isPlaying.observe(this) { playing ->
            playPauseButton.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
        }

        viewModel.currentTime.observe(this) { time ->
            progressTime.text = time
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            track = intent.getParcelableExtra(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            track = intent.getParcelableExtra(TRACK_KEY)
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
            viewModel.prepare(track)
            playPauseButton.isEnabled = true
            playPauseButton.setImageResource(R.drawable.ic_play)
        } ?: run {
            playPauseButton.isEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isPlaying.value == true) {
            viewModel.playbackControl()
        }
    }
}