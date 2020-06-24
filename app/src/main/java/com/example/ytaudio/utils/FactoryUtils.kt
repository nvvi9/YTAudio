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

    fun provideMainActivityViewModel(application: Application) =
        MainActivityViewModel.Factory(
            AudioDatabase.getInstance(application.applicationContext).audioDatabaseDao,
            provideMediaPlaybackServiceConnection(application.applicationContext)
        )

    fun providePlaylistViewModel(mediaId: String, application: Application) =
        PlaylistViewModel.Factory(
            mediaId,
            provideMediaPlaybackServiceConnection(application.applicationContext),
            AudioDatabase.getInstance(application.applicationContext).audioDatabaseDao,
            application
        )

    fun provideAudioPlayerViewModel(application: Application) =
        AudioPlayerViewModel.Factory(
            provideMediaPlaybackServiceConnection(application.applicationContext),
            AudioDatabase.getInstance(application.applicationContext).audioDatabaseDao,
            application
        )
}