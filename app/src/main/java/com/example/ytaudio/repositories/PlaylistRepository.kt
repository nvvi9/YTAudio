package com.example.ytaudio.repositories

import androidx.lifecycle.LiveData
import com.example.ytaudio.database.entities.AudioInfo


interface PlaylistRepository {

    val audioInfoList: LiveData<List<AudioInfo>>

    suspend fun deleteAudioInfo(audioId: List<String>)
}