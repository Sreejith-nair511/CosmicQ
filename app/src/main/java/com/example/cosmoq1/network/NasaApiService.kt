package com.example.cosmoq1.network

import com.example.cosmoq1.data.ApodResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String = "DEMO_KEY"
    ): ApodResponse
}
