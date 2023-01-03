package com.shkarov.splashphotos.di

import android.app.Application
import androidx.room.Room
import com.shkarov.splashphotos.database.PhotosDatabase
import com.shkarov.splashphotos.database.data.collection_photo_db.CollectionPhotoDbDao
import com.shkarov.splashphotos.database.data.collections_db.CollectionDbDao
import com.shkarov.splashphotos.database.data.photo_db.PhotoDbDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Provides
    @Singleton
    fun providesDataBase(application: Application): PhotosDatabase {
        return Room.databaseBuilder(
            application,
            PhotosDatabase::class.java,
            PhotosDatabase.DB_NAME
        )
            .build()
    }

    @Provides
    fun providesPhotoDao(db: PhotosDatabase): PhotoDbDao {
        return db.photoDb()
    }

    @Provides
    fun providesCollectionPhotoDao(db: PhotosDatabase): CollectionPhotoDbDao {
        return db.collectionPhotoDb()
    }

    @Provides
    fun providesCollectionDao(db: PhotosDatabase): CollectionDbDao {
        return db.collectionDb()
    }
}
