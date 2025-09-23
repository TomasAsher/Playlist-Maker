package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    single<TrackInteractor> { TrackInteractorImpl(get()) }
    single<PlayerInteractor> { PlayerInteractorImpl(get()) }
    single<ThemeInteractor> { ThemeInteractorImpl(get()) }
}