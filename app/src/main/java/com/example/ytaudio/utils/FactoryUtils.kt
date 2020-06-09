package com.example.ytaudio.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.screens.audio_player.AudioPlayerViewModel
import com.example.ytaudio.screens.playlist.PlaylistViewModel
import com.example.ytaudio.service.MediaPlaybackService
import com.example.ytaudio.service.MediaPlaybackServiceConnection

object FactoryUtils {

    private fun provideMediaPlaybackServiceConnection(context: Context) =
        MediaPlaybackServiceConnection.getInstance(
            context,
            ComponentName(context, MediaPlaybackService::class.java)
        )

    fun providePlaylistViewModel(application: Application): PlaylistViewModel.Factory {
        val dataSource = AudioDatabase.getInstance(application.applicationContext).audioDatabaseDao
        return PlaylistViewModel.Factory(dataSource, application)
    }

    fun provideAudioPlayerViewModel(application: Application): AudioPlayerViewModel.Factory {
        val dataSource = AudioDatabase.getInstance(application.applicationContext).audioDatabaseDao
        return AudioPlayerViewModel.Factory(dataSource, application)
    }
}