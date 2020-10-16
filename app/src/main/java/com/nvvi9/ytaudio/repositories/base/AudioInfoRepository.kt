package com.nvvi9.ytaudio.repositories.base

import androidx.lifecycle.LiveData
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo


interface AudioInfoRepository {
    suspend fun addToPlaylist(vararg id: String)
    suspend fun deleteFromPlaylist(vararg id: String)
    fun getPlaylist(): LiveData<List<AudioInfo>>
}