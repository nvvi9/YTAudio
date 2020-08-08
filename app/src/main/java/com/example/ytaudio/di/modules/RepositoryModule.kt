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
    abstract fun bindPlaylistRepository(playlistRepository: PlaylistRepository): Repository

    @ExperimentalPagingApi
    @Binds
    @Singleton
    abstract fun bindYouTubeRepository(youTubeRepository: YouTubeRepository): Repository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(searchRepository: SearchRepository): Repository

    @Binds
    @Singleton
    abstract fun bindAudioInfoRepository(audioInfoRepository: AudioInfoRepository): Repository
}