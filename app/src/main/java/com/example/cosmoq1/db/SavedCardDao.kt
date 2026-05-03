package com.example.cosmoq1.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: SavedCardEntity)

    @Query("DELETE FROM saved_cards WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM saved_cards ORDER BY rowid DESC")
    fun getAllSaved(): Flow<List<SavedCardEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_cards WHERE id = :id)")
    suspend fun isSaved(id: String): Boolean
}
