package com.example.ytaudio.service.library

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.service.MEDIA_ROOT_ID
import com.example.ytaudio.service.extensions.toMediaMetadataList
import com.example.ytaudio.utils.Event


class DatabaseAudioSource(
    database: AudioDatabaseDao,
    val itemsUpdated: (parentId: String) -> Unit
) : AbstractAudioSource() {

    private var currentAudioInfo = mutableListOf<AudioInfo>()

    private val databaseAudioInfo = database.getAllAudio()

    private val databaseObserver = Observer<List<AudioInfo>> {
        if (it != currentAudioInfo) {
            state = STATE_INITIALIZING
            _itemsUpdatedEvent.value = Event(it.toMediaMetadataList())
            state = STATE_INITIALIZED
            itemsUpdated(MEDIA_ROOT_ID)
        }
    }

    init {
        state = STATE_INITIALIZING
        databaseAudioInfo.observeForever(databaseObserver)
    }

    override fun iterator(): Iterator<MediaMetadataCompat> =
        currentAudioInfo.toMediaMetadataList().iterator()

    private val _itemsUpdatedEvent = MutableLiveData<Event<List<MediaMetadataCompat>>>()
    val itemsUpdatedEvent: LiveData<Event<List<MediaMetadataCompat>>>
        get() = _itemsUpdatedEvent
}