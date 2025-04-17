package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupToolbar()
        handleIntent()
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
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_player)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun handleIntent() {
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        progressTime.text = track.formatTime()
        durationTime.text = track.formatTime()

        track.collectionName?.let { albumName.text = it }
        track.releaseDate?.let { releaseYear.text = it.take(4) }
        track.primaryGenreName?.let { genreLabel.text = it }
        track.country?.let { countryLabel.text = it }
    }
}