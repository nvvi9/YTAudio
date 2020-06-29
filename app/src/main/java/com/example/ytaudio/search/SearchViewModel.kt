package com.example.ytaudio.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.network.ApiService
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.utils.extensions.mapParallel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val databaseDao: AudioDatabaseDao
) : ViewModel() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private val _searchItemList = MutableLiveData<List<SearchItem>>()
    val searchItemList: LiveData<List<SearchItem>>
        get() = _searchItemList

    private val _autoComplete = MutableLiveData<List<String>>()
    val autoComplete: LiveData<List<String>>
        get() = _autoComplete

    fun setResponse(query: String, maxResults: Int = 25) {
        coroutineScope.launch {
            try {
                val ytResponse = ApiService.ytService.getYTResponse(query).await()
                _searchItemList.value = ytResponse.searchItemList
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

    fun insertInDatabase(items: List<SearchItem>) {
        coroutineScope.launch {
            databaseDao.insert(items.mapParallel {
                YTExtractor().extractAudioInfo(it.videoId)
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