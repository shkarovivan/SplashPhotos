package com.shkarov.splashphotos.collection_photos

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.database.data.photo_db.PhotoDb
import com.shkarov.splashphotos.isNetworkConnected
import com.shkarov.splashphotos.unsplash_api.UnsplashRepository
import com.shkarov.splashphotos.unsplash_api.data.collections.CollectionUnsplash
import com.shkarov.splashphotos.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CollectionPhotosViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository: UnsplashRepository
) : ViewModel() {

    private val detailCollectionLiveData = MutableLiveData<CollectionUnsplash>()
    private val photosLiveData = MutableLiveData<List<PhotoDb>>()


    private val isLoadingPhoto = MutableLiveData<Boolean>()
    private val toastSingleLiveEvent = SingleLiveEvent<Int>()
    private val isNetworkSingleLiveEvent = SingleLiveEvent<Int>()
    private val errorLiveData = MutableLiveData<String>()

    private var pageNumber: Int = 0
    private var savedListPhoto = mutableListOf<PhotoDb>()

    val detailCollection: LiveData<CollectionUnsplash>
        get() = detailCollectionLiveData

    val photos: LiveData<List<PhotoDb>>
        get() = photosLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingPhoto

    val error: LiveData<String>
        get() = errorLiveData

    val toastLiveData: LiveData<Int>
        get() = toastSingleLiveEvent

    val isNetworkLiveData: LiveData<Int>
        get() = isNetworkSingleLiveEvent

    init {
        if (!isNetwork()) {
            isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
        }
    }

    fun getCollectionInfo(id: String) {
        if (!isNetwork()) {
            isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoadingPhoto.postValue(true)
                detailCollectionLiveData.postValue(unsplashRepository.getCollectionInfo(id))
                isLoadingPhoto.postValue(false)
            } catch (t: Throwable) {
                isLoadingPhoto.postValue(false)
                errorLiveData.postValue(t.toString())
            }
            isLoadingPhoto.postValue(false)
        }
    }

    fun getCollectionPhotos(id: String) {
        if (!isNetwork()) {
            isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                pageNumber++
                isLoadingPhoto.postValue(true)
                val savedList = photosLiveData.value ?: emptyList()
                val listPhoto = unsplashRepository.loadCollectionPhotos(id, pageNumber)
                photosLiveData.postValue(savedList + listPhoto)
                isLoadingPhoto.postValue(false)
                unsplashRepository.saveListCollectionsPhotos(id, listPhoto, pageNumber)

            } catch (t: Throwable) {
                Timber.tag("NetworkUnsplash").d("$t")
                try {
                    pageNumber--
                    val listPhoto = unsplashRepository.getListCollectionPhotosFromDb(id)
                    if (listPhoto.isNotEmpty()){
                        photosLiveData.postValue(listPhoto)
                        toastSingleLiveEvent.postValue(R.string.memory_data_loaded_text)
                        isLoadingPhoto.postValue(false)
                        savedListPhoto += listPhoto
                    } else throw Throwable(t)
                } catch (t: Throwable) {
                    isLoadingPhoto.postValue(false)
                    errorLiveData.postValue(t.toString())
                }
            }
            isLoadingPhoto.postValue(false)
        }
    }

    private fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }

    fun getPageNumber(): Int = pageNumber

}