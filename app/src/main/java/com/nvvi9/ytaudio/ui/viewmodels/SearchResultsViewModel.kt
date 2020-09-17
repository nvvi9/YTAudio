package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@Singleton
class SearchResultsViewModel
@Inject constructor(
    private val useCases: YouTubeUseCases,
    private val audioInfoRepository: AudioInfoRepository
) : ViewModel() {

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>>
        get() = _errorEvent

    fun getFromQuery(query: String) =
        useCases.getYouTubeItemsFromQuery(query)

    fun addToPlaylist(id: String) {
        viewModelScope.launch {
            try {
                audioInfoRepository.insertIntoDatabase(id)
            } catch (t: Throwable) {
                _errorEvent.value = Event(t.toString())
            }
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