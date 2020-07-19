package com.example.ytaudio.repositories.base

import androidx.lifecycle.LiveData
import com.example.ytaudio.data.audioinfo.AudioInfo


interface PlaylistRepository {

    val audioInfoList: LiveData<List<AudioInfo>>

    suspend fun deleteAudioInfo(audioId: List<String>)
}