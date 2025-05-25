package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.impl.SearchHistory
import com.example.playlistmaker.data.impl.ThemePreferences
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
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

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}