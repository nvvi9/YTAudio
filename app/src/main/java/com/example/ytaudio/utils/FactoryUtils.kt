package com.example.ytaudio.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.service.MediaPlaybackService
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.viewmodels.AudioPlayerViewModel
import com.example.ytaudio.viewmodels.MainActivityViewModel
import com.example.ytaudio.viewmodels.PlaylistViewModel

object FactoryUtils {

    private fun provideMediaPlaybackServiceConnection(context: Context) =
        MediaPlaybackServiceConnection.getInstance(
            context,
            ComponentName(context, MediaPlaybackService::class.java)
        )

    private fun provideAudioDatabaseDao(context: Context) =
        AudioDatabase.getInstance(context).audioDatabaseDao

    fun provideMainActivityViewModel(application: Application): MainActivityViewModel.Factory {
        val mediaPlaybackServiceConnection =
            provideMediaPlaybackServiceConnection(application.applicationContext)
        return MainActivityViewModel.Factory(mediaPlaybackServiceConnection)
    }

    fun providePlaylistViewModel(application: Application): PlaylistViewModel.Factory {
        val dataSource = provideAudioDatabaseDao(application.applicationContext)
        return PlaylistViewModel.Factory(dataSource, application)
    }

    fun provideAudioPlayerViewModel(application: Application): AudioPlayerViewModel.Factory {
        val dataSource = provideAudioDatabaseDao(application.applicationContext)
        return AudioPlayerViewModel.Factory(dataSource, application)
    }
}