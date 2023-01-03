package com.shkarov.splashphotos.profile.user_photos


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb
import com.shkarov.splashphotos.isNetworkConnected
import com.shkarov.splashphotos.unsplash_api.UnsplashRepository
import com.shkarov.splashphotos.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPhotosViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository: UnsplashRepository
) : ViewModel() {

    private val userPhotosLiveData = MutableLiveData<List<PhotoDb>>()
    private val isAllPhotosLoadedLiveData = MutableLiveData<Boolean>()
    private val isLoadingPhoto = MutableLiveData<Boolean>()
    private val errorSingleLiveEvent = SingleLiveEvent<Int>()

    private val pageSize = 10
    private var photosPage: Int = 0
    var totalPhotos: Long = 0
    var isInit = false

    val userPhotos: LiveData<List<PhotoDb>>
        get() = userPhotosLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingPhoto

    val errorLiveData: LiveData<Int>
        get() = errorSingleLiveEvent

    val isAllPhotosLoaded: LiveData<Boolean>
        get() = isAllPhotosLoadedLiveData

    fun getUserPhotos(userName: String) {
        if (!isNetwork()) {
            errorSingleLiveEvent.postValue(R.string.network_error_text)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                photosPage++
                isLoadingPhoto.postValue(true)
                val savedList = userPhotosLiveData.value ?: emptyList()
                val userPhotos = unsplashRepository.getUserPhotos(userName, photosPage,)
                userPhotosLiveData.postValue(savedList+userPhotos)
                isAllPhotosLoadedLiveData.postValue(photosPage * pageSize > totalPhotos)
                isLoadingPhoto.postValue(false)
            } catch (t: Throwable) {
                isLoadingPhoto.postValue(false)
                errorSingleLiveEvent.postValue(R.string.network_error_text)
                photosPage--
            }
            isLoadingPhoto.postValue(false)
        }
    }

    private fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }
}