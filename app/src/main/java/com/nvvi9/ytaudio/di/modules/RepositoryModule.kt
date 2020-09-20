package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.repositories.*
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@Module
@ExperimentalCoroutinesApi
@FlowPreview
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