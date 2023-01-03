package com.shkarov.splashphotos.database.data.collection_photo_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shkarov.splashphotos.database.data.collections_db.CollectionDbContract

@Dao
interface CollectionPhotoDbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCollectionPhotos(photos: List<CollectionPhotoDb>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionPhoto(collectionPhoto: CollectionPhotoDb)

    @Query("SELECT * FROM ${CollectionPhotoDbContract.TABLE_NAME} WHERE ${CollectionPhotoDbContract.Columns.COLLECTION_ID} = :collectionId")
    suspend fun getPhotosByCollectionId(collectionId: String): List<CollectionPhotoDb>

    @Query("DELETE  FROM ${CollectionPhotoDbContract.TABLE_NAME} WHERE ${CollectionPhotoDbContract.Columns.COLLECTION_ID} = :collectionId")
    suspend fun removeAllPhotosByCollectionId(collectionId: String)

    @Query("DELETE  FROM ${CollectionDbContract.TABLE_NAME}")
    suspend fun removeAllCollectionsPhotos()
}