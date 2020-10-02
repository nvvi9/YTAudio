package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.repositories.Repository
import com.nvvi9.ytaudio.repositories.SearchRepository
import com.nvvi9.ytaudio.repositories.YouTubeRepository
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@Module
@ExperimentalCoroutinesApi
@FlowPreview
abstract class RepositoryModule {

    @ExperimentalPagingApi
    @Binds
    abstract fun bindYouTubeRepository(youTubeRepository: YouTubeRepository): Repository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(searchRepository: SearchRepository): Repository

    @Binds
    abstract fun bindAudioInfoRepository(audioInfoRepository: AudioInfoRepository): Repository
}