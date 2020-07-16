package com.example.ytaudio.repositories

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.Transformations
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.utils.extensions.toMediaMetadataList
import com.example.ytaudio.utils.extensions.toPlaylistItemList
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistRepository @Inject constructor(databaseDao: AudioDatabaseDao) {

    private val availableAudioInfo = Transformations.map(databaseDao.getAllAudio()) { list ->
        list.filterNot { it.needUpdate }
    }

    val metadataList: List<MediaMetadataCompat>
        get() = mediaMetadataList.value ?: emptyList()

    val mediaMetadataList =
        Transformations.map(availableAudioInfo) { it.toMediaMetadataList() }

    val playlistItemList =
        Transformations.map(availableAudioInfo) { it.toPlaylistItemList() }
}