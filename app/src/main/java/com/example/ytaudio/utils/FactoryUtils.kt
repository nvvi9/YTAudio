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

    fun provideMainActivityViewModel(context: Context): MainActivityViewModel.Factory {
        val mediaPlaybackServiceConnection =
            provideMediaPlaybackServiceConnection(context.applicationContext)
        return MainActivityViewModel.Factory(mediaPlaybackServiceConnection)
    }

    fun providePlaylistViewModel(
        audioId: String,
        context: Context,
        application: Application
    ): PlaylistViewModel.Factory {
        val mediaPlaybackServiceConnection =
            provideMediaPlaybackServiceConnection(context)
        val dataSource = provideAudioDatabaseDao(context)
        return PlaylistViewModel.Factory(
            audioId,
            mediaPlaybackServiceConnection,
            dataSource,
            application
        )
    }

    fun provideAudioPlayerViewModel(
        context: Context,
        application: Application
    ): AudioPlayerViewModel.Factory {
        val mediaPlaybackServiceConnection =
            provideMediaPlaybackServiceConnection(context.applicationContext)
        val dataSource = provideAudioDatabaseDao(context.applicationContext)
        return AudioPlayerViewModel.Factory(
            mediaPlaybackServiceConnection,
            dataSource,
            application
        )
    }
}