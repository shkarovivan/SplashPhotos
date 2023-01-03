package com.shkarov.splashphotos.database.data.photo_db

import androidx.room.*

@Dao
interface PhotoDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPhotos(photos: List<PhotoDb>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoDb)

    @Query("SELECT * FROM ${PhotoDbContract.TABLE_NAME}")
    suspend fun getAllPhotos(): List<PhotoDb>

    @Query("DELETE  FROM ${PhotoDbContract.TABLE_NAME}")
    suspend fun removeAllPhotos()
}
