package com.shkarov.splashphotos.profile

import android.app.Application
import androidx.lifecycle.*
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.database.data.collections_db.CollectionDb
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb
import com.shkarov.splashphotos.isNetworkConnected
import com.shkarov.splashphotos.unsplash_api.UnsplashRepository
import com.shkarov.splashphotos.unsplash_api.data.user.UserInfo
import com.shkarov.splashphotos.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository : UnsplashRepository
) : ViewModel() {

    private val userPhotosLiveData = MutableLiveData<List<PhotoDb>>()
    private val userLikedPhotosLiveData = MutableLiveData<List<PhotoDb>>()
    private val userCollectionsLiveData = MutableLiveData<List<CollectionDb>>()

    private val isAllPhotosLoadedLiveData = MutableLiveData<Boolean>()
    private val isLikedPhotosLoadedLiveData = MutableLiveData<Boolean>()
    private val isCollectionsLoadedLiveData = MutableLiveData<Boolean>()

    private val userInfoLiveData = MutableLiveData<UserInfo>()

    private val isLoadingPhoto = MutableLiveData<Boolean>()
    private val errorSingleLiveEvent = SingleLiveEvent<Int>()

    private var isUserInfo: Boolean = false
    private var userName: String? = null
    private var totalPhotos: Long = 0
    private var totalLiked: Long = 0
    private var totalCollections: Long = 0
    private var photosPage: Int = 0
    private var likedPage: Int = 0
    private var collectionsPage: Int = 0
    private val pageSize = 10

    val userPhotos: LiveData<List<PhotoDb>>
        get() = userPhotosLiveData

    val userLikedPhotos: LiveData<List<PhotoDb>>
        get() = userLikedPhotosLiveData

    val userCollections: LiveData<List<CollectionDb>>
        get() = userCollectionsLiveData

    val userInfo: LiveData<UserInfo>
        get() = userInfoLiveData

    val isAllPhotosLoaded: LiveData<Boolean>
        get() = isAllPhotosLoadedLiveData
    val isAllLikedPhotosLoaded: LiveData<Boolean>
        get() = isLikedPhotosLoadedLiveData
    val isAllCollectionsLoaded: LiveData<Boolean>
        get() = isCollectionsLoadedLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingPhoto

    val errorLiveData: LiveData<Int>
        get() = errorSingleLiveEvent

    init {
        if (!isNetwork()) {
            errorSingleLiveEvent.postValue(R.string.network_error_text)
        } else {
            getUserInfo()
        }
    }

    fun getUserName() = userName
    fun getTotalPhotos() = totalPhotos
    fun getTotalLiked() = totalLiked
    fun getTotalCollections() = totalCollections
    fun getPhotosPage() = photosPage
    fun getLikedPage() = likedPage
    fun getCollectionsPage() = collectionsPage

    fun getUserInfo() {
        if (!isNetwork()) {
            errorSingleLiveEvent.postValue(R.string.network_error_text)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoadingPhoto.postValue(true)
                val userInfo = unsplashRepository.getUserInfo()
                userInfoLiveData.postValue(userInfo)
                userName = userInfo.userName
                isUserInfo = true
                totalPhotos = userInfo.totalPhotos
                totalLiked = userInfo.totalLikes
                totalCollections = userInfo.totalCollections
                isLoadingPhoto.postValue(false)
            } catch (t: Throwable) {
                isLoadingPhoto.postValue(false)
                errorSingleLiveEvent.postValue(R.string.network_error_text)
            }
            isLoadingPhoto.postValue(false)
        }
    }

    fun getUserPhotos(userName: String) {
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

    fun getUserLikedPhotos(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                likedPage++
                isLoadingPhoto.postValue(true)
                val savedList = userLikedPhotosLiveData.value ?: emptyList()
                val likedPhotos = unsplashRepository.getUserLikedPhotos(userName, likedPage, pageSize)
                userLikedPhotosLiveData.postValue(savedList+likedPhotos)
                isLikedPhotosLoadedLiveData.postValue(likedPage * pageSize > totalLiked)
                isLoadingPhoto.postValue(false)
            } catch (t: Throwable) {
                isLoadingPhoto.postValue(false)
                errorSingleLiveEvent.postValue(R.string.network_error_text)
                likedPage--
            }
            isLoadingPhoto.postValue(false)
        }
    }

    fun getUserCollections(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                collectionsPage++
                isLoadingPhoto.postValue(true)
                val savedList = userCollectionsLiveData.value ?: emptyList()
                val userCollections = unsplashRepository.getUserCollections(userName,collectionsPage)
                userCollectionsLiveData.postValue(savedList+userCollections)
                isCollectionsLoadedLiveData.postValue(collectionsPage * pageSize > totalCollections)
                isLoadingPhoto.postValue(false)
            } catch (t: Throwable) {
                collectionsPage--
                isLoadingPhoto.postValue(false)
                errorSingleLiveEvent.postValue(R.string.network_error_text)
            }
            isLoadingPhoto.postValue(false)
        }
    }

    private fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }
}