package com.example.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.ytaudio.domain.YouTubeUseCases
import com.example.ytaudio.repositories.AudioInfoRepository
import com.example.ytaudio.utils.Event
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalPagingApi
class YouTubeViewModel @Inject constructor(
    private val useCases: YouTubeUseCases,
    private val audioInfoRepository: AudioInfoRepository
) : ViewModel() {

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>>
        get() = _errorEvent

    fun getRecommended(): Flow<PagingData<YouTubeItem>> =
        useCases.getRecommendedYouTubeItems()

    fun addToPlaylist(id: String) {
        viewModelScope.launch {
            audioInfoRepository.insertIntoDatabase(id)
        }
    }

    fun deleteFromPlaylist(id: String) {
        viewModelScope.launch {
            try {
                audioInfoRepository.deleteById(id)
            } catch (t: Throwable) {
                _errorEvent.value = Event(t.toString())
            }
        }
    }
}