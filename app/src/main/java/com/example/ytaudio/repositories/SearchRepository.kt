package com.example.ytaudio.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.network.ApiService
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.utils.LiveContentException
import com.example.ytaudio.utils.UriAliveTimeMissException
import com.example.ytaudio.utils.extensions.forEachParallel
import com.example.ytaudio.utils.extensions.mapParallel
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
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

    private val audioInfoList = mutableSetOf<AudioInfo>()

    suspend fun setItemsFromResponse(query: String, maxResults: Int = 50) {
        withContext(Dispatchers.IO) {
            val list =
                ApiService.ytService.getYTResponseAsync(query, maxResults).await()
                    .items.map { SearchItem.from(it) }
            _searchItemList.postValue(list)

            list.forEachParallel(Dispatchers.IO) { searchItem ->
                try {
                    YTExtractor().extractAudioInfo(searchItem.videoId)
                } catch (e: ExtractionException) {
                    Log.e(javaClass.simpleName, "id: $searchItem extraction failed")
                    null
                } catch (e: YoutubeRequestException) {
                    Log.e(javaClass.simpleName, "network failure")
                    null
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, e.toString())
                    null
                }?.let {
                    audioInfoList.add(it)
                }
            }
        }
    }

    suspend fun setAutocomplete(query: String) {
        withContext(Dispatchers.IO) {
            val list =
                ApiService.autoCompleteService.getAutoCompleteAsync(query).await()
                    .items?.mapNotNull { it.suggestion?.data }

            _autoCompleteList.postValue(list)
        }
    }

    suspend fun addDatabase(items: List<SearchItem>) {
        withContext(Dispatchers.IO) {
            val list = items.mapParallel(Dispatchers.IO) { searchItem ->
                try {
                    audioInfoList.find { audioInfo ->
                        audioInfo.youtubeId == searchItem.videoId
                    } ?: YTExtractor().extractAudioInfo(searchItem.videoId)
                } catch (e: ExtractionException) {
                    Log.e(javaClass.simpleName, "id: ${searchItem.videoId} extraction failed")
                    null
                } catch (e: YoutubeRequestException) {
                    Log.e(javaClass.simpleName, "network failure")
                    null
                } catch (e: LiveContentException) {
                    Log.e(javaClass.simpleName, e.message!!)
                    null
                } catch (e: UriAliveTimeMissException) {
                    Log.e(javaClass.simpleName, e.message!!)
                    null
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, e.toString())
                    null
                }
            }.filterNotNull()

            database.insert(list)
        }
    }
}