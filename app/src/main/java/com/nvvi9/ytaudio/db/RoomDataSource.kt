package com.nvvi9.ytaudio.db

import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import javax.inject.Inject


class RoomDataSource @Inject constructor(private val audioInfoDao: AudioInfoDao) {

    suspend fun addToDatabase(audioInfo: AudioInfo): Boolean =
        try {
            audioInfoDao.insert(audioInfo)
            true
        } catch (t: Throwable) {
            false
        }

    suspend fun deleteFromDatabase(id: String): Boolean =
        try {
            audioInfoDao.deleteById(id)
            true
        } catch (t: Throwable) {
            false
        }

    fun getData() = audioInfoDao.getAllAudio()
}