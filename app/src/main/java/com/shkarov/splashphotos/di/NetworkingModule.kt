package com.shkarov.splashphotos.di

import com.shkarov.splashphotos.network.AuthorizationInterceptor
import com.shkarov.splashphotos.unsplash_api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Provides
    @Singleton
    fun providesNetworking(): UnsplashApi
    {
        val okhttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(
                HttpLoggingInterceptor()
                {
                    Timber.tag("NetworkUnsplash").d(it)
                }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .followRedirects(true)
            .addNetworkInterceptor(AuthorizationInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://google.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okhttpClient)
            .build()

        return retrofit.create() ?: error("retrofit is not initialized")
    }


}
