package com.example.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.example.ytaudio.repositories.PlaylistRepository
import com.example.ytaudio.repositories.PlaylistRepositoryImpl
import com.example.ytaudio.repositories.YouTubeRepository
import com.example.ytaudio.repositories.YouTubeRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(playlistRepositoryImpl: PlaylistRepositoryImpl): PlaylistRepository

    @ExperimentalPagingApi
    @Binds
    @Singleton
    abstract fun bindYouTubeRepository(youTubeRepositoryImpl: YouTubeRepositoryImpl): YouTubeRepository
}