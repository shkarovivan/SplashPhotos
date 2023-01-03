package com.shkarov.splashphotos.photo_detail_info

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.isNetworkConnected
import com.shkarov.splashphotos.unsplash_api.UnsplashRepository
import com.shkarov.splashphotos.unsplash_api.data.detail_photo.DetailPhoto
import com.shkarov.splashphotos.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DetailPhotoViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository : UnsplashRepository,
) : ViewModel() {

    private val detailPhotoLiveData = MutableLiveData<DetailPhoto>()
    private val isLoadingPhoto = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<String>()
    private val likeLiveData = MutableLiveData<Boolean>()
    private val saveSuccessSingleLiveEvent = SingleLiveEvent<Uri>()
    private val toastSingleLiveEvent = SingleLiveEvent<Int>()

    private var isLoaded: Boolean = false
    private var isWorkManagerStartedLiveData = MutableLiveData<Boolean>()

    val isWorkManagerStarted: LiveData<Boolean>
        get() = isWorkManagerStartedLiveData

    val detailPhoto: LiveData<DetailPhoto>
        get() = detailPhotoLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingPhoto

    val error: LiveData<String>
        get() = errorLiveData

    val like: LiveData<Boolean>
        get() = likeLiveData

    val saveSuccessLiveData: LiveData<Uri>
        get() = saveSuccessSingleLiveEvent

    val toastLiveData: LiveData<Int>
        get() = toastSingleLiveEvent


    fun getDetailPhotoInfo(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!isNetwork()) {
                    throw RuntimeException(application.getString(R.string.network_error_text))
                }
                isLoadingPhoto.postValue(true)
                val detailPhotoInfo = unsplashRepository.getDetailPhotoInfo(id)
                detailPhotoLiveData.postValue(detailPhotoInfo)
                isLoadingPhoto.postValue(false)
                isLoaded = true
            } catch (t: Throwable) {
                isLoadingPhoto.postValue(false)
                errorLiveData.postValue(t.toString())
            }
        }
    }

    fun isLoadedInfo(): Boolean = isLoaded

    fun changeLike() {
        if (isNetwork()) {
            if (detailPhoto.value != null) {
                val id = detailPhoto.value!!.id
                val like = detailPhoto.value!!.likedByUser
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        if (!like) {
                            unsplashRepository.setLike(id)
                            detailPhoto.value!!.likedByUser = true
                            likeLiveData.postValue(true)
                        } else {
                            unsplashRepository.deleteLike(id)
                            detailPhoto.value!!.likedByUser = false
                            likeLiveData.postValue(false)
                        }
                    } catch (t: Throwable) {
                        errorLiveData.postValue(t.toString())
                    }
                }
            }
        } else {
            toastSingleLiveEvent.postValue(R.string.network_error_text)
        }
    }

    fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }

    fun startDownload(url: String, name: String) {

        val workData = workDataOf(
            DownloadWorker.DOWNLOAD_URL_KEY to url,
            DownloadWorker.DOWNLOAD_NAME_KEY to name
        )

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workData)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1000, TimeUnit.MILLISECONDS)
            .setConstraints(workConstraints)
            .build()
        WorkManager.getInstance(application)
            .enqueueUniqueWork(DOWNLOAD_WORK_ID, ExistingWorkPolicy.KEEP, workRequest)

        isWorkManagerStartedLiveData.postValue(true)
    }

    companion object {
        private const val DOWNLOAD_WORK_ID = "download work"
    }

}