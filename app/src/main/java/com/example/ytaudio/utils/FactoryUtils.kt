package com.example.ytaudio.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import com.example.ytaudio.activity.MainActivityViewModel
import com.example.ytaudio.player.PlayerViewModel
import com.example.ytaudio.playlist.PlaylistViewModel
import com.example.ytaudio.search.SearchViewModel
import com.example.ytaudio.service.MediaPlaybackService
import com.example.ytaudio.service.MediaPlaybackServiceConnection


object FactoryUtils {

    private fun provideMediaPlaybackServiceConnection(context: Context) =
        MediaPlaybackServiceConnection.getInstance(
            context,
            ComponentName(context, MediaPlaybackService::class.java)
        )

    fun provideMainActivityViewModel(application: Application) =
        MainActivityViewModel.Factory(
            application,
            provideMediaPlaybackServiceConnection(application.applicationContext)
        )

    fun providePlaylistViewModel(mediaId: String, application: Application) =
        PlaylistViewModel.Factory(
            mediaId,
            provideMediaPlaybackServiceConnection(application.applicationContext),
            application
        )

    fun provideAudioPlayerViewModel(application: Application) =
        PlayerViewModel.Factory(
            provideMediaPlaybackServiceConnection(application.applicationContext),
            application
        )

    fun provideSearchViewModel(application: Application) =
        SearchViewModel.Factory(application)
}