package com.shkarov.splashphotos.di

import android.app.Application
import android.content.Context
import com.shkarov.splashphotos.photo_detail_info.SavePhotoRepository
import com.shkarov.splashphotos.unsplash_api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SavePhotoRepositoryModule {

    @Singleton
    @Provides
    fun providesSavePhotoRepository(appContext: Application, api: UnsplashApi): SavePhotoRepository =
        SavePhotoRepository(appContext,api)
}