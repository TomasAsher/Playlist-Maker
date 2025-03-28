package com.example.playlistmaker

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TrackResponse
}