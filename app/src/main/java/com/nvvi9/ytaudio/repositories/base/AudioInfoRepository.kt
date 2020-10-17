package com.nvvi9.ytaudio.repositories.base

import androidx.lifecycle.LiveData
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo


interface AudioInfoRepository {
    suspend fun addToPlaylist(id: String): Boolean
    suspend fun deleteFromPlaylist(id: String): Boolean
    fun getPlaylist(): LiveData<List<AudioInfo>>
}