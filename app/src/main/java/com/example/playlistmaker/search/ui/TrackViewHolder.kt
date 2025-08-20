package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackViewHolder(parent: ViewGroup, inflater: LayoutInflater) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_track, parent, false)) {
    private val trackCover: ImageView = itemView.findViewById(R.id.trackCover)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.formatTime()

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(trackCover)
    }
}