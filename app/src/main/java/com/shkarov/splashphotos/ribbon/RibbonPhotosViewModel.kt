package com.shkarov.splashphotos.ribbon

import android.app.Application
import androidx.lifecycle.*
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
class RibbonPhotosViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository : UnsplashRepository
) : ViewModel() {

    private val photosLiveData = MutableLiveData<List<PhotoDb>>()

    private val isLoadingPhoto = MutableLiveData<Boolean>()
    private val toastSingleLiveEvent = SingleLiveEvent<Int>()
    private val isNetworkSingleLiveEvent = SingleLiveEvent<Int>()


    private var pageNumber: Int = 0

    val photos: LiveData<List<PhotoDb>>
        get() = photosLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingPhoto

    val toastLiveData: LiveData<Int>
        get() = toastSingleLiveEvent

    val isNetworkLiveData: LiveData<Int>
        get() = isNetworkSingleLiveEvent

    init {
        if (!isNetwork()) {
            isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
        } else {
            getListPhotos()
        }
    }

    fun getListPhotos() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!isNetwork()) {
                    throw RuntimeException(application.getString(R.string.network_error_text))
                }
                pageNumber++
                isLoadingPhoto.postValue(true)
                val savedList = photosLiveData.value ?: emptyList()
                val listPhoto = unsplashRepository.loadListPhotos(pageNumber)
                photosLiveData.postValue(savedList+listPhoto)
                isLoadingPhoto.postValue(false)
                unsplashRepository.saveListPhotos(listPhoto, pageNumber)

            } catch (t: Throwable) {
                try {
                    pageNumber--
                    val listPhoto = unsplashRepository.getListPhotosFromDb()
                    if (listPhoto.isNotEmpty()){
                    photosLiveData.postValue(listPhoto)
                    toastSingleLiveEvent.postValue(R.string.memory_data_loaded_text)
                    isLoadingPhoto.postValue(false)
                    }
                    else throw Throwable(t)
                } catch (t: Throwable) {
                    isLoadingPhoto.postValue(false)
                    isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
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