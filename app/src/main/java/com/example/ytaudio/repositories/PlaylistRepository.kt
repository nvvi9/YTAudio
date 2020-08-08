package com.example.ytaudio.repositories

import com.example.ytaudio.db.AudioInfoDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(private val audioInfoDao: AudioInfoDao) : Repository {

    fun getAudioInfo() =
        audioInfoDao.getAllAudio()
}