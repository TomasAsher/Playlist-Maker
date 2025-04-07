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
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar: Toolbar = findViewById(R.id.toolbar_player)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track")
        }
        track?.let { setupPlayer(it) }
    }

    private fun setupPlayer(track: Track) {
        val albumCover: ImageView = findViewById(R.id.albumCover)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.track_placeholder)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen._8dp)))
            .into(albumCover)

        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.progressTime).text = track.formatTime()

        findViewById<TextView>(R.id.durationTime).text = track.formatTime()
        track.collectionName?.let {
            findViewById<TextView>(R.id.albumName).text = it
        }
        track.releaseDate?.let {
            findViewById<TextView>(R.id.releaseYear).text = it.take(4)
        }
        track.primaryGenreName?.let {
            findViewById<TextView>(R.id.genreLabel).text = it
        }
        track.country?.let {
            findViewById<TextView>(R.id.countryLabel).text = it
        }
    }
}