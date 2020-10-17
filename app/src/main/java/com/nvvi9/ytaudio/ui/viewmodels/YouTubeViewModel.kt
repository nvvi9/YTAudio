package com.nvvi9.ytaudio.ui.viewmodels

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.nvvi9.ytaudio.domain.AudioInfoUseCase
import com.nvvi9.ytaudio.domain.YouTubeUseCase
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class YouTubeViewModel @Inject constructor(
    private val audioInfoUseCase: AudioInfoUseCase,
    private val youTubeUseCase: YouTubeUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val addJob = Job()
    private val deleteJob = Job()

    private val addScope = CoroutineScope(mainDispatcher + addJob)
    private val deleteScope = CoroutineScope(mainDispatcher + deleteJob)

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>>
        get() = _errorEvent

    private val youTubeItems = MutableLiveData<PagingData<YouTubeItem>>()

    private val items = MediatorLiveData<PagingData<YouTubeItem>>()

    private val playlistItemsId = audioInfoUseCase.getItemsId()

    override fun onCleared() {
        super.onCleared()
        removeSources()
        deleteJob.cancel()
        addJob.cancel()
    }

    fun removeSources() {
        items.run {
            removeSource(youTubeItems)
            removeSource(playlistItemsId)
        }
    }

    fun observeOnYouTubeItems(owner: LifecycleOwner, observer: Observer<PagingData<YouTubeItem>>) {
        items.observe(owner, observer)

        items.addSource(youTubeItems) {
            it?.let {
                items.postValue(it)
            }
        }

        items.addSource(playlistItemsId) { id ->
            youTubeItems.value?.map {
                it.isAdded = id.contains(it.id)
                it
            }?.let {
                items.postValue(it)
            }
        }
    }

    fun updateYTItems(query: String? = null) {
        viewModelScope.launch(ioDispatcher) {
            loadItems(query)
                .filterNotNull()
                .cachedIn(this)
                .collectLatest { youTubeItems.postValue(it) }
        }
    }

    fun addToPlaylist(id: String) {
        addScope.launch(ioDispatcher) {
            try {
                audioInfoUseCase.addToPlaylist(id)
            } catch (t: Throwable) {
                _errorEvent.postValue(Event("Error occurred"))
            }
        }
    }

    fun deleteFromPlaylist(id: String) {
        deleteScope.launch(ioDispatcher) {
            try {
                audioInfoUseCase.deleteFromPlaylist(id)
            } catch (t: Throwable) {
                _errorEvent.postValue(Event("Error occurred"))
            }
        }
    }

    private fun loadItems(query: String? = null) =
        query?.let { youTubeUseCase.getYouTubeItemsFromQuery(it) }
            ?: youTubeUseCase.getPopularYouTubeItems()
}