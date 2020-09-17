package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.ui.adapters.YTLoadState
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@ExperimentalPagingApi
class YouTubeViewModel
@Inject constructor(
    private val useCases: YouTubeUseCases,
    private val audioInfoRepository: AudioInfoRepository
) : ViewModel() {

    private var job: Job? = null

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>>
        get() = _errorEvent

    private val _recommendedItems = MutableLiveData<PagingData<YouTubeItem>>()
    val recommendedItems: LiveData<PagingData<YouTubeItem>> get() = _recommendedItems

    private val _loadState = MutableLiveData<YTLoadState>(YTLoadState.Empty)
    val loadState: LiveData<YTLoadState> get() = _loadState

    fun updateRecommended() {
        job?.run {
            _loadState.postValue(YTLoadState.Loading)
            cancel()
        }
        job = viewModelScope.launch {
            useCases.getRecommendedYouTubeItems().collectLatest {
                _recommendedItems.postValue(it)
                _loadState.postValue(YTLoadState.LoadingDone)
            }
        }
    }

    fun addToPlaylist(id: String) {
        viewModelScope.launch {
            try {
                audioInfoRepository.insertIntoDatabase(id)
            } catch (t: Throwable) {
                t.message?.let {
                    _errorEvent.value = Event(it)
                }
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