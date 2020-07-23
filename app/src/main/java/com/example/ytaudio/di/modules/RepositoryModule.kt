package com.example.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.example.ytaudio.repositories.*
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

    @Binds
    @Singleton
    abstract fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository
}