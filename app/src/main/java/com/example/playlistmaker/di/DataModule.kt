package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.ThemePreferences
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val THEME_PREFS_NAME = "theme_prefs"
private const val APP_PREFS_NAME = "app_prefs"

val dataModule = module {
    single<Gson> { Gson() }

    single { androidContext().getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE) }
    single { androidContext().getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE) }

    single<ThemeRepository> { ThemePreferences(get()) }
    single<SearchHistory> { SearchHistory(get(), get()) }
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<PlayerRepository> { PlayerRepositoryImpl() }
}