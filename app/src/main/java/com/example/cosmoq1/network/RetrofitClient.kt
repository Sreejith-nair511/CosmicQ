package com.example.cosmoq1.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private fun buildClient(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(buildClient())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object RetrofitClient {
    val spaceNewsApi: SpaceNewsApi by lazy {
        retrofit("https://api.spaceflightnewsapi.net/v4/").create(SpaceNewsApi::class.java)
    }
    val nasaApi: NasaApi by lazy {
        retrofit("https://api.nasa.gov/").create(NasaApi::class.java)
    }
    val spaceXApi: SpaceXApi by lazy {
        retrofit("https://api.spacexdata.com/v4/").create(SpaceXApi::class.java)
    }
}
