package com.example.ytaudio.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.network.NetworkService
import com.example.ytaudio.utils.extensions.forEachParallel
import com.example.ytaudio.utils.extensions.mapParallel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SearchRepository(context: Context) : BaseRepository(context) {

    private val _searchItemList = MutableLiveData<List<SearchItem>>()
    val searchItemList: LiveData<List<SearchItem>>
        get() = _searchItemList

    private val _autoCompleteList = MutableLiveData<List<String>>()
    val autoCompleteList: LiveData<List<String>>
        get() = _autoCompleteList

    private val audioInfoList = mutableSetOf<AudioInfo>()

    suspend fun setItemsFromResponse(query: String, maxResults: Int = 50) {
        withContext(Dispatchers.IO) {
            val list = NetworkService.ytService.getYTResponseAsync(query, maxResults).await()
                .items.map { SearchItem.from(it) }
            _searchItemList.postValue(list)

            list.forEachParallel(Dispatchers.IO) { searchItem ->
                extractAudioInfo(searchItem.videoId)?.let {
                    audioInfoList.add(it)
                }
            }
        }
    }

    suspend fun setAutocomplete(query: String) {
        withContext(Dispatchers.IO) {
            val list =
                NetworkService.autoCompleteService.getAutoCompleteAsync(query).await()
                    .items?.mapNotNull { it.suggestion?.data }

            _autoCompleteList.postValue(list)
        }
    }

    suspend fun addDatabase(items: List<SearchItem>) {
        withContext(Dispatchers.IO) {
            val list = items.mapParallel(Dispatchers.IO) { searchItem ->
                audioInfoList.find { audioInfo ->
                    audioInfo.youtubeId == searchItem.videoId
                } ?: extractAudioInfo(searchItem.videoId)
            }.filterNotNull()

            databaseDao.insert(list)
        }
    }
}