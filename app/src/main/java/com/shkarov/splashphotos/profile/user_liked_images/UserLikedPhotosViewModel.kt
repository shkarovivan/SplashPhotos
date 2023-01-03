package com.shkarov.splashphotos.profile.user_liked_images

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
class UserLikedPhotosViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository: UnsplashRepository
) : ViewModel() {

    private val userLikedPhotosLiveData  = MutableLiveData<List<PhotoDb>>()
    private val isLikedPhotosLoadedLiveData = MutableLiveData<Boolean>()
    private val isLoadingPhoto = MutableLiveData<Boolean>()
    private val errorSingleLiveEvent = SingleLiveEvent<Int>()

    private val pageSize = 10
    private var likedPage: Int = 0
    var totalLiked: Long = 0
    var isInit = false

    val userLikedPhotos: LiveData<List<PhotoDb>>
        get() = userLikedPhotosLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingPhoto

    val errorLiveData: LiveData<Int>
        get() = errorSingleLiveEvent

    val isAllPhotosLoaded: LiveData<Boolean>
        get() = isLikedPhotosLoadedLiveData

    fun getUserLikedPhotos(userName: String) {
        if (!isNetwork()) {
            errorSingleLiveEvent.postValue(R.string.network_error_text)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                likedPage++
                isLoadingPhoto.postValue(true)
                val savedList = userLikedPhotosLiveData.value ?: emptyList()
                val likedPhotos = unsplashRepository.getUserLikedPhotos(userName, likedPage, pageSize)
                userLikedPhotosLiveData.postValue(savedList+likedPhotos)
                isLikedPhotosLoadedLiveData.postValue(likedPage * pageSize >= totalLiked)
                isLoadingPhoto.postValue(false)
            } catch (t: Throwable) {
                isLoadingPhoto.postValue(false)
                errorSingleLiveEvent.postValue(R.string.network_error_text)
                likedPage--
            }
            isLoadingPhoto.postValue(false)
        }
    }

    private fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }
}