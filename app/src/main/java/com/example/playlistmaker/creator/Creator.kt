package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackInteractorImpl
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.ThemePreferences
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.google.gson.Gson

object Creator {
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    }

    private fun provideSearchHistorySharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun provideGson(): Gson {
        return Gson()
    }

    private fun provideThemeRepository(context: Context): ThemeRepository {
        return ThemePreferences(getSharedPreferences(context))
    }

    private fun provideSearchHistory(context: Context): SearchHistory {
        return SearchHistory(provideSearchHistorySharedPreferences(context), provideGson())
    }

    private fun provideTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(provideSearchHistory(context))
    }

    private fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl(provideMediaPlayer())
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(provideTrackRepository(context))
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(provideThemeRepository(context))
    }
}

class MyApp : Application()