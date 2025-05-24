package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.impl.SearchHistory
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private fun provideSearchHistory(context: Context): SearchHistory {
        return SearchHistory(getSharedPreferences(context))
    }

    private fun provideTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(provideSearchHistory(context))
    }

    private fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(provideTrackRepository(context))
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }
}

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}