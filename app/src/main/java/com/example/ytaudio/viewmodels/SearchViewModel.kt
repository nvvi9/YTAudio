package com.example.ytaudio.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.network.ApiService
import com.example.ytaudio.network.youtube.VideoItem
import com.example.ytaudio.network.youtube.YTResponse
import com.example.ytaudio.utils.getAudioInfo
import com.example.ytaudio.utils.mapParallel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val databaseDao: AudioDatabaseDao
) : ViewModel() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val _ytResponse = MutableLiveData<YTResponse>()
    val ytResponse: LiveData<YTResponse>
        get() = _ytResponse

    private val _autoComplete = MutableLiveData<List<String>>()
    val autoComplete: LiveData<List<String>>
        get() = _autoComplete

    fun setResponse(query: String, maxResults: Int = 25) {
        coroutineScope.launch {
            val getResponseDeferred = ApiService.ytService.getYTResponse(query)
            try {
                val resultResponse = getResponseDeferred.await()
                _ytResponse.value = resultResponse
            } catch (t: Throwable) {
                Log.i("SearchViewModel", t.message ?: "error")
            }
        }
    }

    fun setAutoComplete(query: String) {
        coroutineScope.launch {
            try {
                val getAutoCompleteDeferred =
                    ApiService.autoCompleteService.getAutoComplete(query)
                val result = getAutoCompleteDeferred.await()
                _autoComplete.value = result.items?.mapNotNull { it.suggestion?.data }
            } catch (t: Throwable) {
                Log.i("SearchViewModel", t.message ?: "error")
            }
        }
    }

    fun insertInDatabase(items: List<VideoItem>) {
        coroutineScope.launch {
            databaseDao.insert(items.mapParallel {
                getAudioInfo(it.id.videoId)
            })
        }
    }

    class Factory(private val databaseDao: AudioDatabaseDao) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(databaseDao) as T
        }
    }
}