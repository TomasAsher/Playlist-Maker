package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()
    private var track: Track? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)
        setupToolbar()
        getTrackFromArguments()
        setupPlayer()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbarPlayer.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun getTrackFromArguments() {
        track = arguments?.getParcelable(TRACK_KEY)
    }

    private fun setupPlayer() {
        track?.let { track ->
            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.track_placeholder)
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen._8dp)))
                .into(binding.albumCover)

            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.durationTime.text = track.formatTime()
            binding.progressTime.text = getString(R.string._0_00)

            track.collectionName?.let { binding.albumName.text = it }
            track.releaseDate?.let { binding.releaseYear.text = it.take(4) }
            track.primaryGenreName?.let { binding.genreLabel.text = it }
            track.country?.let { binding.countryLabel.text = it }

            if (!track.previewUrl.isNullOrEmpty()) {
                viewModel.prepare(track)
                binding.playPauseButton.isEnabled = true
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
            } else {
                binding.playPauseButton.isEnabled = false
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
            }

            binding.playPauseButton.setOnClickListener {
                if (binding.playPauseButton.isEnabled) {
                    viewModel.playbackControl()
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            binding.playPauseButton.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
        }

        viewModel.currentTime.observe(viewLifecycleOwner) { time ->
            binding.progressTime.text = time
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isPlaying.value == true) {
            viewModel.playbackControl()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TRACK_KEY = "track"

        fun newInstance(track: Track): PlayerFragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TRACK_KEY, track)
                }
            }
        }
    }
}