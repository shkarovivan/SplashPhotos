package com.shkarov.splashphotos.profile

import androidx.lifecycle.*
import com.shkarov.splashphotos.sign_in.TokenStorage
import com.shkarov.splashphotos.unsplash_api.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val unsplashRepository :UnsplashRepository
) : ViewModel() {

    private val deletedSuccessLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<String>()


    val deletedSuccess: LiveData<Boolean>
        get() = deletedSuccessLiveData

    val error: LiveData<String>
        get() = errorLiveData

    fun deleteDirs() {
        TokenStorage.accessToken = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                unsplashRepository.cleanData()
                deletedSuccessLiveData.postValue(true)
            } catch (t: Throwable) {
                errorLiveData.postValue(t.toString())
                deletedSuccessLiveData.postValue(false)
            }
        }
    }
}



