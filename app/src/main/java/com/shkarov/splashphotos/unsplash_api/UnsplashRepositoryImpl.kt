package com.shkarov.splashphotos.unsplash_api

import android.app.Application
import android.os.Environment
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.database.data.collection_photo_db.CollectionPhotoDb
import com.shkarov.splashphotos.database.data.collection_photo_db.CollectionPhotoDbDao
import com.shkarov.splashphotos.database.data.collections_db.CollectionDb
import com.shkarov.splashphotos.database.data.collections_db.CollectionDbDao
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb
import com.shkarov.splashphotos.database.data.photo_db.PhotoDbDao
import com.shkarov.splashphotos.unsplash_api.data.collections.CollectionUnsplash
import com.shkarov.splashphotos.unsplash_api.data.detail_photo.DetailPhoto
import com.shkarov.splashphotos.unsplash_api.data.list_photos.PhotoUnsplash
import com.shkarov.splashphotos.unsplash_api.data.user.UserInfo
import java.io.File
import javax.inject.Inject

class UnsplashRepositoryImpl @Inject constructor(
    private val appContext: Application,
    private val api: UnsplashApi,
    private val photoDatabaseDao: PhotoDbDao,
    private val collectionDatabaseDao: CollectionDbDao,
    private val collectionPhotosDatabaseDao: CollectionPhotoDbDao
) : UnsplashRepository {

    override suspend fun loadCollectionPhotos(id: String, page: Int): List<PhotoDb> {
        val listPhoto = api.getCollectionPhotos(id, page)
        return convertListPhotoUnsplashToListPhotoDb(listPhoto)
    }

    override suspend fun loadListCollections(page: Int): List<CollectionDb> {
        val listCollection = api.getListCollections(page)
        return convertListCollectionUnsplashToListCollectionDb(listCollection)
    }

    override suspend fun loadListSearchPhotos(query: String, page: Int): List<PhotoDb> {
        val listPhoto = api.searchPhotos(query, page).results
        return convertListPhotoUnsplashToListPhotoDb(listPhoto)
    }

    override suspend fun loadListPhotos(page: Int): List<PhotoDb> {
        val listPhoto = api.getListPhotos(page)
        return convertListPhotoUnsplashToListPhotoDb(listPhoto)
    }


    override suspend fun saveListCollections(listCollectionDb: List<CollectionDb>, page: Int) {
        val directory = getUnsplashFilesDirectory()
        if (directory != null) {
            if (page == 1) {
                removeAllCollectionsFromDb()
            }

            listCollectionDb.forEach { collectionDb ->
                collectionDb.coverImagePath = saveImage(
                    collectionDb.coverImagePath,
                    collectionDb.id + appContext.resources.getString(R.string.photo_extension),
                    directory
                )

                collectionDb.profileImagePath = saveImage(
                    collectionDb.profileImagePath,
                    collectionDb.userAvatar + appContext.resources.getString(R.string.photo_extension),
                    directory
                )
            }
            insertListCollectionsToDb(listCollectionDb)
        }
    }

    override suspend fun saveListPhotos(listPhotoDb: List<PhotoDb>, page: Int) {

        val directory = getUnsplashFilesDirectory()
        if (directory != null) {
            if (page == 1) {
                removeAllPhotoFromDb()
            }

            listPhotoDb.forEach { photoDb ->
                photoDb.photoPath = saveImage(
                    photoDb.photoPath,
                    photoDb.id + appContext.resources.getString(R.string.photo_extension),
                    directory
                )

                photoDb.profileImagePath = saveImage(
                    photoDb.profileImagePath,
                    photoDb.userAvatar + appContext.resources.getString(R.string.photo_extension),
                    directory
                )
            }
            insertListPhotoToDb(listPhotoDb)
        }
    }

    override suspend fun saveListCollectionsPhotos(
        collectionId: String,
        listPhotoDb: List<PhotoDb>,
        page: Int
    ) {
        val directory = getUnsplashFilesDirectory()
        if (directory != null) {
            if (page == 1) {
                removeAllCollectionPhotosFromDb(collectionId)
            }

            val collectionsListPhotos = mutableListOf<CollectionPhotoDb>()
            listPhotoDb.forEach { photoDb ->
                photoDb.photoPath = saveImage(
                    photoDb.photoPath,
                    photoDb.id + appContext.resources.getString(R.string.photo_extension),
                    directory
                )

                photoDb.profileImagePath = saveImage(
                    photoDb.profileImagePath,
                    photoDb.userAvatar + appContext.resources.getString(R.string.photo_extension),
                    directory
                )
                collectionsListPhotos += convertToCollectionPhotoDb(collectionId, photoDb)
            }
            insertCollectionPhotosToDb(collectionsListPhotos)
        }
    }

    override suspend fun getUserInfo(): UserInfo = api.getUserInfo()


    override suspend fun getUserPhotos(userName: String, page: Int): List<PhotoDb> {
        val listPhoto = api.getUserPhotos(userName, page)
        return convertListPhotoUnsplashToListPhotoDb(listPhoto)
    }

    override suspend fun getUserLikedPhotos(
        userName: String,
        page: Int,
        pageSize: Int
    ): List<PhotoDb> {
        val listPhoto = api.getUserLikedPhotos(userName, page, pageSize)
        return convertListPhotoUnsplashToListPhotoDb(listPhoto)
    }

    override suspend fun getUserCollections(userName: String, page: Int): List<CollectionDb> {
        val listCollection = api.getUserCollections(userName, page)
        return convertListCollectionUnsplashToListCollectionDb(listCollection)
    }

    private fun getUnsplashFilesDirectory(): File? {
        return if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            appContext.filesDir
        } else {
            appContext.getExternalFilesDir(null)
        }
    }

    override suspend fun cleanData(){
        cleanBD()
        deleteDirs()
    }

    private suspend fun cleanBD() {
        photoDatabaseDao.removeAllPhotos()
        collectionDatabaseDao.removeAllCollections()
        collectionPhotosDatabaseDao.removeAllCollectionsPhotos()
    }

     private fun deleteDirs() {
        appContext.filesDir.deleteRecursively()
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            appContext.getExternalFilesDir(null)?.deleteRecursively()
        }
    }


    private suspend fun saveImage(fileURL: String, fileName: String, directory: File): String {
        val file = File(
            directory, fileName
        )
        try {
            file.outputStream().use { fileOutputStream ->
                val response =
                    api.getFile(fileURL)
                response.byteStream()
                    .use { inputStream ->
                        inputStream.copyTo(fileOutputStream)
                    }
            }
            return file.absolutePath
        } catch (t: Throwable) {
            file.delete()
            error(t)
        }
    }

    private suspend fun insertListPhotoToDb(photoList: List<PhotoDb>) {
        photoDatabaseDao.insertListPhotos(photoList)
    }

    private suspend fun removeAllPhotoFromDb() {
        val photos = photoDatabaseDao.getAllPhotos()
        photos.forEach {
            File(it.photoPath).delete()
            File(it.profileImagePath).delete()
        }
        photoDatabaseDao.removeAllPhotos()
    }

    private suspend fun removeAllCollectionsFromDb() {
        val photos = collectionDatabaseDao.getAllCollections()
        photos.forEach {
            File(it.coverImagePath).delete()
            File(it.profileImagePath).delete()
        }
        collectionDatabaseDao.removeAllCollections()
    }

    private suspend fun removeAllCollectionPhotosFromDb(id: String) {
        val photos = collectionPhotosDatabaseDao.getPhotosByCollectionId(id)
        photos.forEach {
            File(it.photoPath).delete()
            File(it.profileImagePath).delete()
        }
        collectionPhotosDatabaseDao.removeAllCollectionsPhotos()
    }

    override suspend fun getListPhotosFromDb(): List<PhotoDb> {
        return photoDatabaseDao.getAllPhotos()
    }

    override suspend fun getDetailPhotoInfo(id: String): DetailPhoto =
        api.getPhotoDetailInfo(id)


    override suspend fun getCollectionInfo(collectionId: String): CollectionUnsplash {
        return api.getCollectionInfo(collectionId)
    }


    private suspend fun insertListCollectionsToDb(collectionList: List<CollectionDb>) {
        collectionDatabaseDao.insertListCollections(collectionList)
    }

    override suspend fun getListCollectionsFromDb(): List<CollectionDb> {
        return collectionDatabaseDao.getAllCollections()
    }

    private suspend fun insertCollectionPhotosToDb(photoList: List<CollectionPhotoDb>) {
        collectionPhotosDatabaseDao.insertListCollectionPhotos(photoList)
    }

    override suspend fun getListCollectionPhotosFromDb(collectionId: String): List<PhotoDb> {
        val collectionPhotosDb = collectionPhotosDatabaseDao.getPhotosByCollectionId(collectionId)
        val listPhotoDb = mutableListOf<PhotoDb>()
        collectionPhotosDb.forEach {
            listPhotoDb += convertCollectionPhotoDbToPhotoDb(it)
        }
        return listPhotoDb
    }

    private fun convertToCollectionDb(
        collectionUnsplash: CollectionUnsplash
    ): CollectionDb {
        return CollectionDb(
            id = collectionUnsplash.id,
            title = collectionUnsplash.title,
            totalPhotos = collectionUnsplash.totalPhotos,
            userName = collectionUnsplash.user.name,
            userAvatar = collectionUnsplash.user.userName,
            profileImagePath = collectionUnsplash.user.profileImage.small,
            coverImagePath = collectionUnsplash.coverPhoto.urls.small
        )
    }

    private fun convertListPhotoUnsplashToListPhotoDb(list: List<PhotoUnsplash>): List<PhotoDb> {
        val listPhotoDb: MutableList<PhotoDb> = mutableListOf()
        list.forEach {
            listPhotoDb += convertUnsplashPhotoToPhotoDb(it)
        }
        return listPhotoDb
    }

    private fun convertListCollectionUnsplashToListCollectionDb(list: List<CollectionUnsplash>): List<CollectionDb> {
        val listCollectionDb: MutableList<CollectionDb> = mutableListOf()
        list.forEach {
            listCollectionDb += convertToCollectionDb(it)
        }
        return listCollectionDb
    }

    private fun convertUnsplashPhotoToPhotoDb(
        photoUnsplash: PhotoUnsplash
    ): PhotoDb {

        return PhotoDb(
            id = photoUnsplash.id,
            photoPath = photoUnsplash.urls.small,
            userName = photoUnsplash.user.name,
            userAvatar = photoUnsplash.user.userName,
            profileImagePath = photoUnsplash.user.profileImage.small,
            totalLikes = photoUnsplash.likes,
            likedByUser = photoUnsplash.likedByUser
        )

    }

    private fun convertToCollectionPhotoDb(
        collectionId: String, photoDb: PhotoDb
    ): CollectionPhotoDb {
        return CollectionPhotoDb(
            id = 0,
            collectionId = collectionId,
            photoId = photoDb.id,
            photoPath = photoDb.photoPath,
            userName = photoDb.userName,
            userAvatar = photoDb.userAvatar,
            profileImagePath = photoDb.profileImagePath,
            totalLikes = photoDb.totalLikes,
            likedByUser = photoDb.likedByUser
        )
    }

    private fun convertCollectionPhotoDbToPhotoDb(
        collectionPhotoDb: CollectionPhotoDb
    ): PhotoDb {
        return PhotoDb(
            id = collectionPhotoDb.photoId,
            photoPath = collectionPhotoDb.photoPath,
            userName = collectionPhotoDb.userName,
            userAvatar = collectionPhotoDb.userAvatar,
            profileImagePath = collectionPhotoDb.profileImagePath,
            totalLikes = collectionPhotoDb.totalLikes,
            likedByUser = collectionPhotoDb.likedByUser
        )
    }


    override suspend fun setLike(id: String) {
        api.setLike(id)
    }

    override suspend fun deleteLike(id: String) {
        api.deleteLike(id)
    }
}