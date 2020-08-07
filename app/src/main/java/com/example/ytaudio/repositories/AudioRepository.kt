package com.example.ytaudio.repositories

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.Transformations
import com.example.ytaudio.db.AudioInfoDao
import com.example.ytaudio.utils.extensions.toMediaMetadataList
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AudioRepository @Inject constructor(databaseDao: AudioInfoDao) {

    private val availableAudioInfo =
        Transformations.distinctUntilChanged(
            Transformations.map(databaseDao.getAllAudio()) { list ->
                list.filterNot { it.needUpdate }
            })

    val metadataList: List<MediaMetadataCompat>
        get() = mediaMetadataList.value ?: emptyList()

    val mediaMetadataList =
        Transformations.map(availableAudioInfo) { it.toMediaMetadataList() }
}