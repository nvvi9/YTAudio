package com.example.ytaudio.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.network.ApiService
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.utils.extensions.mapParallel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SearchRepository(context: Context) {

    private val database = AudioDatabase.getInstance(context).audioDatabaseDao

    private val _searchItemList = MutableLiveData<List<SearchItem>>()
    val searchItemList: LiveData<List<SearchItem>>
        get() = _searchItemList

    private val _autoCompleteList = MutableLiveData<List<String>>()
    val autoCompleteList: LiveData<List<String>>
        get() = _autoCompleteList

    suspend fun setItemsFromResponse(query: String, maxResults: Int = 25) {
        withContext(Dispatchers.IO) {
            _searchItemList.value =
                ApiService.ytService.getYTResponse(query, maxResults).await()
                    .items.map { it.toSearchItem() }
        }
    }

    suspend fun setAutocomplete(query: String) {
        withContext(Dispatchers.IO) {
            val autoCompleteString =
                ApiService.autoCompleteService.getAutoComplete(query).await()
                    .items?.mapNotNull { it.suggestion?.data }

            _autoCompleteList.value = autoCompleteString
        }
    }

    suspend fun addToDatabase(items: List<SearchItem>) {
        withContext(Dispatchers.IO) {
            val audioInfoList = items.mapParallel {
                YTExtractor().extractAudioInfo(it.videoId)
            }

            database.insert(audioInfoList)
        }
    }
}