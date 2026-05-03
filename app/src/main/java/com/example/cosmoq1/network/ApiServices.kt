package com.example.cosmoq1.network

import com.example.cosmoq1.data.ApodResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpaceNewsApi {
    @GET("articles")
    suspend fun getArticles(
        @Query("limit")    limit: Int    = 100,
        @Query("offset")   offset: Int   = 0,
        @Query("ordering") ordering: String = "-published_at"
    ): SpaceNewsResponse
}

interface NasaApi {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String = "DEMO_KEY"
    ): ApodResponse
}

interface SpaceXApi {
    @GET("launches/latest")
    suspend fun getLatestLaunch(): SpaceXLaunch
}
