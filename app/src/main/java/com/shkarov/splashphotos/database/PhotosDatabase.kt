package com.shkarov.splashphotos.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shkarov.splashphotos.database.data.collection_photo_db.CollectionPhotoDb
import com.shkarov.splashphotos.database.data.collection_photo_db.CollectionPhotoDbDao
import com.shkarov.splashphotos.database.data.collections_db.CollectionDb
import com.shkarov.splashphotos.database.data.collections_db.CollectionDbDao
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb
import com.shkarov.splashphotos.database.data.photo_db.PhotoDbDao

@Database(entities = [
	PhotoDb::class,
	CollectionDb::class,
	CollectionPhotoDb::class

],
	version = PhotosDatabase.DB_VERSION,
	exportSchema = true)

abstract class PhotosDatabase : RoomDatabase()  {
	abstract fun photoDb(): PhotoDbDao
	abstract fun collectionDb(): CollectionDbDao
	abstract fun collectionPhotoDb(): CollectionPhotoDbDao

	companion object {
		const val DB_VERSION = 1
		const val DB_NAME = "splash-photo-database"
	}
}