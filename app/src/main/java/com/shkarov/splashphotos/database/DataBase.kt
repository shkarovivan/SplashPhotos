package com.shkarov.splashphotos.database

import android.content.Context
import androidx.room.Room


object DataBase {
	lateinit var instance: PhotosDatabase
		private set

	fun init(context: Context) {
		instance = Room.databaseBuilder(context,
			PhotosDatabase::class.java,
			PhotosDatabase.DB_NAME)
			.build()
	}
}