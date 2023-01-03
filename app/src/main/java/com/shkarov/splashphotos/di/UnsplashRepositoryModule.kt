package com.shkarov.splashphotos.di

import com.shkarov.splashphotos.unsplash_api.UnsplashRepository
import com.shkarov.splashphotos.unsplash_api.UnsplashRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class UnsplashRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun providesUnsplashRepository(impl:UnsplashRepositoryImpl): UnsplashRepository
}