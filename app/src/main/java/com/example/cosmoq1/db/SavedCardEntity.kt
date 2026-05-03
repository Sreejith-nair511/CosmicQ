package com.example.cosmoq1.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cards")
data class SavedCardEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val category: String,
    val source: String,
    val publishedAt: String,
    val url: String
)
