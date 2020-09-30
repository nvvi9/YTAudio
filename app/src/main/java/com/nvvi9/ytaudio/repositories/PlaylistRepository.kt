package com.nvvi9.ytaudio.repositories

import com.nvvi9.ytaudio.db.AudioInfoDao
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistRepository @Inject constructor(private val audioInfoDao: AudioInfoDao) : Repository {

    fun getAudioInfo() =
        audioInfoDao.getAllAudio()
}