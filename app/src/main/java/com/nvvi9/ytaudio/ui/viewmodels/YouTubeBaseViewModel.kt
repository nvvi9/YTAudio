package com.nvvi9.ytaudio.ui.viewmodels

import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.utils.Event
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
abstract class YouTubeBaseViewModel : ViewModel() {

    @Inject
    lateinit var audioInfoUseCases: AudioInfoUseCases

    private val addJob = Job()
    private val deleteJob = Job()

    private val addScope = CoroutineScope(Dispatchers.Main + addJob)
    private val deleteScope = CoroutineScope(Dispatchers.Main + deleteJob)

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        deleteJob.cancel()
        addJob.cancel()
    }

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

    init {
        Log.i("YouTubeBaseViewModel", "initialized")
    }

    protected abstract fun loadItems(query: String? = null): Flow<PagingData<YouTubeItem>?>

    fun updateYTItems(query: String? = null) {
        viewModelScope.launch {
            loadItems(query)
                .filterNotNull()
                .cachedIn(this)
                .collectLatest { _youTubeItems.postValue(it) }
        }
    }

    fun addToPlaylist(id: String) {
        addScope.launch {
            try {
                audioInfoUseCases.addToPlaylist(id)
            } catch (t: Throwable) {
                Log.e(javaClass.simpleName, t.stackTraceToString())
            }
        }
    }

    fun deleteFromPlaylist(id: String) {
        deleteScope.launch {
            try {
                audioInfoUseCases.addToPlaylist(id)
            } catch (t: Throwable) {
                Log.e(javaClass.simpleName, t.stackTraceToString())
            }
        }
    }
}