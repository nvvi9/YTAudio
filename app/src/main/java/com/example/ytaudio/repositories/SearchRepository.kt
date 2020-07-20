package com.example.ytaudio.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.db.PlaylistDao
import com.example.ytaudio.network.AutoCompleteService
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.utils.extensions.forEachParallel
import com.example.ytaudio.utils.extensions.mapParallel
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SearchRepository @Inject constructor(
    private val databaseDao: PlaylistDao,
    private val ytService: YouTubeApiService,
    private val autoCompleteService: AutoCompleteService,
    ytExtractor: YTExtractor
) : BaseRepository(ytExtractor) {

    private val _searchItemList = MutableLiveData<List<YouTubeItem>>()
    val youTubeItemList: LiveData<List<YouTubeItem>>
        get() = _searchItemList

    private val _autoCompleteList = MutableLiveData<List<String>>()
    val autoCompleteList: LiveData<List<String>>
        get() = _autoCompleteList

    private val audioInfoList = mutableSetOf<AudioInfo>()

    suspend fun setItemsFromResponse(query: String) {
        withContext(Dispatchers.IO) {
            val list = ytService.getYTSearchResponseAsync(query)
                .items.map { YouTubeItem.from(it) }
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
                autoCompleteService.getAutoCompleteAsync(query).await()
                    .items?.mapNotNull { it.suggestion?.data }

            _autoCompleteList.postValue(list)
        }
    }

    suspend fun addDatabase(items: List<YouTubeItem>) {
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