package com.example.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.mediateka.ui.favorites.FavoritesFragment
import com.example.playlistmaker.mediateka.ui.playlists.PlaylistsFragment
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment(R.layout.fragment_media) {
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMediaBinding.bind(view)
        setupViewPager()
    }

    private fun setupViewPager() {
        val fragments = listOf(
            FavoritesFragment.newInstance() to getString(R.string.favorites),
            PlaylistsFragment.newInstance() to getString(R.string.playlists)
        )

        binding.viewPager.adapter = MediaPagerAdapter(requireActivity(), fragments.map { it.first })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = fragments[position].second
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MediaFragment()
    }
}