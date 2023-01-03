package com.shkarov.splashphotos.profile.user_collections

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shkarov.splashphotos.R
import com.shkarov.splashphotos.database.data.collections_db.CollectionDb
import com.shkarov.splashphotos.isNetworkConnected
import com.shkarov.splashphotos.unsplash_api.UnsplashRepository
import com.shkarov.splashphotos.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserCollectionsViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository: UnsplashRepository
) : ViewModel() {

    private val userCollectionsLiveData  = MutableLiveData<List<CollectionDb>>()
    private val isCollectionsLoadedLiveData = MutableLiveData<Boolean>()
    private val isLoadingCollections = MutableLiveData<Boolean>()
    private val errorSingleLiveEvent = SingleLiveEvent<Int>()

    private val pageSize = 10
    private var collectionsPage: Int = 0
    var totalLiked: Long = 0
    var isInit = false

    val userCollections: LiveData<List<CollectionDb>>
        get() = userCollectionsLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingCollections

    val errorLiveData: LiveData<Int>
        get() = errorSingleLiveEvent

    val isAllCollectionsLoaded: LiveData<Boolean>
        get() = isCollectionsLoadedLiveData

    fun getUserCollections(userName: String) {
        if (!isNetwork()) {
            errorSingleLiveEvent.postValue(R.string.network_error_text)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                collectionsPage++
                isLoadingCollections.postValue(true)
                val savedList = userCollectionsLiveData.value ?: emptyList()
                val collections = unsplashRepository.getUserCollections(userName,collectionsPage)
                userCollectionsLiveData.postValue(savedList+collections)
                isCollectionsLoadedLiveData.postValue(collectionsPage * pageSize >= totalLiked)
                isLoadingCollections.postValue(false)
            } catch (t: Throwable) {
                isLoadingCollections.postValue(false)
                errorSingleLiveEvent.postValue(R.string.network_error_text)
                collectionsPage--
            }
            isLoadingCollections.postValue(false)
        }
    }

    private fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }
}