package com.example.ytaudio.service.library

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.service.extensions.toMediaMetadataList
import com.example.ytaudio.utils.Event

class MediaSource(private val database: AudioDatabaseDao) : Iterable<MediaMetadataCompat> {

    @State
    var state: Int = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it(state == STATE_INITIALIZED)
                    }
                }
            }
        }

    private var currentAudioInfo = mutableListOf<AudioInfo>()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var catalog: List<MediaMetadataCompat> = emptyList()

    override fun iterator(): Iterator<MediaMetadataCompat> =
        catalog.iterator()

    private val databaseAudioInfo = database.getAllAudio()

    private val databaseObserver = Observer<List<AudioInfo>> {
        if (it != currentAudioInfo) {
            state = STATE_INITIALIZING
            updateCatalog(it)
        }
    }

    init {
        state = STATE_INITIALIZING
        databaseAudioInfo.observeForever(databaseObserver)
    }


    private val _itemsAddedEvent = MutableLiveData<Event<List<MediaMetadataCompat>>>()
    val itemsAddedEvent: LiveData<Event<List<MediaMetadataCompat>>>
        get() = _itemsAddedEvent

    private val _itemsRemovedEvent = MutableLiveData<Event<List<MediaMetadataCompat>>>()
    val itemsRemovedEvent: LiveData<Event<List<MediaMetadataCompat>>>
        get() = _itemsRemovedEvent

    private val _itemsUpdatedEvent = MutableLiveData<Event<List<MediaMetadataCompat>>>()
    val itemsUpdatedEvent: LiveData<Event<List<MediaMetadataCompat>>>
        get() = _itemsUpdatedEvent

    private fun updateCatalog(list: List<AudioInfo>) {
        when {
            list.size > currentAudioInfo.size -> {
                _itemsAddedEvent.value = Event((list - currentAudioInfo).toMediaMetadataList())
            }
            list.size < currentAudioInfo.size -> {
                _itemsRemovedEvent.value = Event((currentAudioInfo - list).toMediaMetadataList())
            }
            else -> {
                _itemsUpdatedEvent.value = Event(list.toMediaMetadataList())
            }
        }
        state = STATE_INITIALIZED
    }
    fun whenReady(performAction: (Boolean) -> Unit): Boolean =
        when (state) {
            STATE_CREATED, STATE_INITIALIZING -> {
                onReadyListeners += performAction
                false
            }
            else -> {
                performAction(state != STATE_ERROR)
                true
            }
        }
}
