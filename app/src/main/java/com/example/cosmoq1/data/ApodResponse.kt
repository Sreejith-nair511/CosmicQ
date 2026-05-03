package com.example.cosmoq1.data

import com.google.gson.annotations.SerializedName

data class ApodResponse(
    @SerializedName("title") val title: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("url") val url: String,
    @SerializedName("hdurl") val hdUrl: String?,
    @SerializedName("date") val date: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("copyright") val copyright: String?
)
