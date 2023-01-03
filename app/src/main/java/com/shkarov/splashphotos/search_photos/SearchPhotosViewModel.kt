package com.shkarov.splashphotos.search_photos

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
class SearchPhotosViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository: UnsplashRepository,
) : ViewModel() {

    private val photosLiveData = MutableLiveData<List<PhotoDb>>()
    private val isLoadingPhoto = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<String>()
    private val isNetworkSingleLiveEvent = SingleLiveEvent<Int>()

    private var pageNumber: Int = 0

    val photos: LiveData<List<PhotoDb>>
        get() = photosLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingPhoto

    val error: LiveData<String>
        get() = errorLiveData

    val isNetworkLiveData: LiveData<Int>
        get() = isNetworkSingleLiveEvent

    init {
        if (!isNetwork()) {
            isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
        }
    }

    fun getListPhotos(query: String) {
        if (!isNetwork()) {
            isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                pageNumber++
                isLoadingPhoto.postValue(true)
                val savedList = photosLiveData.value ?: emptyList()
                val listPhoto = unsplashRepository.loadListSearchPhotos(query, pageNumber)
                photosLiveData.postValue(savedList + listPhoto)
                isLoadingPhoto.postValue(false)
                unsplashRepository.saveListPhotos(listPhoto, pageNumber)

            } catch (t: Throwable) {
                pageNumber--
                isLoadingPhoto.postValue(false)
                errorLiveData.postValue(t.toString())
            }
            isLoadingPhoto.postValue(false)
        }
    }

    private fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }

    fun getPageNumber(): Int = pageNumber
}