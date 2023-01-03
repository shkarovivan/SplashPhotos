package com.shkarov.splashphotos.database.data.collections_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CollectionDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCollections(photos: List<CollectionDb>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionDb)

    @Query("SELECT * FROM ${CollectionDbContract.TABLE_NAME}")
    suspend fun getAllCollections(): List<CollectionDb>

    @Query("DELETE  FROM ${CollectionDbContract.TABLE_NAME}")
    suspend fun removeAllCollections()
}