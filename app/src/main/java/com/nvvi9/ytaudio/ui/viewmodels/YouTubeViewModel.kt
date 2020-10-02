package com.nvvi9.ytaudio.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class YouTubeViewModel @Inject constructor(
    private val audioInfoUseCases: AudioInfoUseCases,
    private val youTubeUseCases: YouTubeUseCases,
    application: Application
) : AndroidViewModel(application) {

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>>
        get() = _errorEvent

    private val _youTubeItems = MutableLiveData<PagingData<YouTubeItem>>()
    val youTubeItems = Transformations.switchMap(_youTubeItems) { ytPaging ->
        Transformations.map(audioInfoUseCases.getItemsId()) { id ->
            ytPaging.map {
                it.isAdded = id.contains(it.id)
                it
            }
        }
    }

    fun updateYTItems(query: String? = null) {
        viewModelScope.launch {
            youTubeUseCases.run {
                query?.let {
                    getYouTubeItemsFromQuery(it)
                } ?: getPopularYouTubeItems()
            }.cachedIn(this).collectLatest {
                _youTubeItems.postValue(it)
            }
        }
    }
}