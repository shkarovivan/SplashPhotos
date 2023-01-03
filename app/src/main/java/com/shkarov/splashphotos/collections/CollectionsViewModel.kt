package com.shkarov.splashphotos.collections

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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val application: Application,
    private val unsplashRepository: UnsplashRepository
) : ViewModel() {

    private val collectionsLiveData = MutableLiveData<List<CollectionDb>>()

    private val isLoadingCollection = MutableLiveData<Boolean>()
    private val toastSingleLiveEvent = SingleLiveEvent<Int>()
    private val isNetworkSingleLiveEvent = SingleLiveEvent<Int>()

    private var pageNumber: Int = 0

    val collections: LiveData<List<CollectionDb>>
        get() = collectionsLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingCollection

    val toastLiveData: LiveData<Int>
        get() = toastSingleLiveEvent

    val isNetworkLiveData: LiveData<Int>
        get() = isNetworkSingleLiveEvent

    init {
        getListCollections()
    }

    fun getListCollections() {
        viewModelScope.launch(Dispatchers.IO) {
            pageNumber++
            try {
                if (!isNetwork()) {
                    throw RuntimeException(application.getString(R.string.network_error_text))
                }
                isLoadingCollection.postValue(true)
                val savedList = collectionsLiveData.value ?: emptyList()
                val listCollection = unsplashRepository.loadListCollections(pageNumber)
                collectionsLiveData.postValue(savedList + listCollection)
                isLoadingCollection.postValue(false)
                unsplashRepository.saveListCollections(listCollection, pageNumber)

            } catch (t: Throwable) {
                Timber.tag("NetworkUnsplash").d("$t")
                try {
                    pageNumber--
                    val listCollection = unsplashRepository.getListCollectionsFromDb()
                    if (listCollection.isNotEmpty()) {
                        collectionsLiveData.postValue(listCollection)
                        toastSingleLiveEvent.postValue(R.string.memory_data_loaded_text)
                        isLoadingCollection.postValue(false)
                    } else throw Throwable(t)
                } catch (t: Throwable) {
                    isLoadingCollection.postValue(false)
                    isNetworkSingleLiveEvent.postValue(R.string.network_error_text)
                }
            }
            isLoadingCollection.postValue(false)
        }
    }

    private fun isNetwork(): Boolean {
        return isNetworkConnected(application)
    }

    fun getPageNumber(): Int = pageNumber
}