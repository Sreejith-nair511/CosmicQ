package com.example.cosmoq1.network

import com.google.gson.annotations.SerializedName

data class SpaceNewsResponse(
    @SerializedName("count")   val count: Int,
    @SerializedName("results") val results: List<SpaceNewsArticle>
)

data class SpaceNewsArticle(
    @SerializedName("id")           val id: Int,
    @SerializedName("title")        val title: String,
    @SerializedName("summary")      val summary: String,
    @SerializedName("url")          val url: String,
    @SerializedName("image_url")    val imageUrl: String,
    @SerializedName("news_site")    val newsSite: String,
    @SerializedName("published_at") val publishedAt: String
)

data class SpaceXLaunch(
    @SerializedName("id")          val id: String,
    @SerializedName("name")        val name: String,
    @SerializedName("details")     val details: String?,
    @SerializedName("date_utc")    val dateUtc: String,
    @SerializedName("links")       val links: SpaceXLinks?
)

data class SpaceXLinks(
    @SerializedName("patch")    val patch: SpaceXPatch?,
    @SerializedName("webcast")  val webcast: String?,
    @SerializedName("article")  val article: String?
)

data class SpaceXPatch(
    @SerializedName("small") val small: String?,
    @SerializedName("large") val large: String?
)
