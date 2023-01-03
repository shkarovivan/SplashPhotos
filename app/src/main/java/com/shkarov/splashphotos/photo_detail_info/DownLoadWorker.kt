package com.shkarov.splashphotos.photo_detail_info

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: SavePhotoRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        var errorData: Data? = null
        var uriData: Data? = null
        var uri: Uri
        coroutineScope {
            withContext(Dispatchers.IO) {
                val urlToDownload = inputData.getString(DOWNLOAD_URL_KEY)
                val nameToDownLoad = inputData.getString(DOWNLOAD_NAME_KEY)
                if (urlToDownload != null && nameToDownLoad != null) {
                    try {
                        uri = repository.savePhoto(urlToDownload, nameToDownLoad)
                        uriData = Data.Builder()
                            .putString(DOWNLOAD_URI_KEY, uri.toString())
                            .build()
                    } catch (t: Throwable) {
                        errorData = Data.Builder()
                            .putString(DOWNLOAD_ERROR_KEY, t.toString())
                            .build()
                    }
                } else {
                    errorData = Data.Builder()
                        .putString(DOWNLOAD_ERROR_KEY, "URL is NULL")
                        .build()
                }
            }
        }
        return if (errorData != null) {
            Result.failure(errorData!!)
        } else {
            if (uriData != null) {
                Result.success(uriData!!)
            } else Result.success()
        }
    }

    companion object {
        const val DOWNLOAD_URL_KEY = "download url"
        const val DOWNLOAD_NAME_KEY = "download_name"
        const val DOWNLOAD_ERROR_KEY = "error key"
        const val DOWNLOAD_URI_KEY = "error key"
    }
}