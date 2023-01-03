package com.shkarov.splashphotos.photo_detail_info

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.documentfile.provider.DocumentFile
import com.shkarov.splashphotos.haveQ
import com.shkarov.splashphotos.unsplash_api.UnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SavePhotoRepository @Inject constructor(
    private val appContext: Application,
    private val api: UnsplashApi
) {

    suspend fun savePhoto(url: String, name: String): Uri {
        var photoUri: Uri
        if (name.isNotEmpty() && url.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                photoUri = savePhotoDetails(name)
                downloadPhoto(url, photoUri)
                makePhotoVisible(photoUri)
            }
            return photoUri
        } else {
            throw RuntimeException("name or url is empty")
        }
    }

    private suspend fun downloadPhoto(url: String, uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                appContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    api.getFile(url)
                        .byteStream()
                        .use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                }
            } catch (t: Throwable) {
                val srcDoc = DocumentFile.fromSingleUri(appContext, uri)
                srcDoc?.delete()
                throw RuntimeException("$t")
            }
        }
    }

    private fun savePhotoDetails(name: String): Uri {
        val volume = if (haveQ()) {
            val storages = MediaStore.getExternalVolumeNames(appContext).toTypedArray()
            if (storages.size > 1) {
                storages[1]
            } else {
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            }
        } else {
            EXTERNAL_CONTENT_URI.toString()
        }

        val photoCollectionUri = MediaStore.Images.Media.getContentUri(volume)
        val photoDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (haveQ()) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        return appContext.contentResolver.insert(photoCollectionUri, photoDetails)!!
    }

    private fun makePhotoVisible(photoUri: Uri) {
        if (haveQ()) {
            val photoDetails = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            appContext.contentResolver.update(photoUri, photoDetails, null, null)
        }
    }
}