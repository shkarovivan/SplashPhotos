package com.shkarov.splashphotos.unsplash_api

import com.shkarov.splashphotos.database.data.collections_db.CollectionDb
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb
import com.shkarov.splashphotos.unsplash_api.data.collections.CollectionUnsplash
import com.shkarov.splashphotos.unsplash_api.data.detail_photo.DetailPhoto
import com.shkarov.splashphotos.unsplash_api.data.user.UserInfo

interface UnsplashRepository {
    suspend fun loadCollectionPhotos(id: String, page: Int): List<PhotoDb>
    suspend fun loadListCollections(page: Int): List<CollectionDb>
    suspend fun loadListSearchPhotos(query: String, page: Int): List<PhotoDb>
    suspend fun loadListPhotos(page: Int): List<PhotoDb>
    suspend fun saveListCollections(listCollectionDb: List<CollectionDb>, page: Int)
    suspend fun cleanData()
    suspend fun saveListPhotos(listPhotoDb: List<PhotoDb>, page: Int)
    suspend fun saveListCollectionsPhotos(collectionId: String,listPhotoDb: List<PhotoDb>, page: Int)
    suspend fun getUserInfo(): UserInfo
    suspend fun getUserPhotos(userName: String, page: Int): List<PhotoDb>
    suspend fun getUserLikedPhotos(userName: String, page: Int, pageSize: Int): List<PhotoDb>
    suspend fun getUserCollections(userName: String, page: Int): List<CollectionDb>
    suspend fun getListPhotosFromDb(): List<PhotoDb>
    suspend fun getDetailPhotoInfo(id: String): DetailPhoto
    suspend fun getCollectionInfo(collectionId: String): CollectionUnsplash
    suspend fun getListCollectionsFromDb(): List<CollectionDb>
    suspend fun getListCollectionPhotosFromDb(collectionId: String): List<PhotoDb>
    suspend fun setLike(id: String)
    suspend fun deleteLike(id: String)

}