package com.example.cosmoq1.data

enum class CardCategory { NEWS, ROCKET, DISCOVERY, MISSION, NASA }

data class SwipeCard(
    val id: String,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val category: CardCategory,
    val source: String,
    val publishedAt: String,
    val url: String = ""
)
