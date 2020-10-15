package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.repositories.AudioInfoRepositoryImpl
import com.nvvi9.ytaudio.repositories.SearchRepositoryImpl
import com.nvvi9.ytaudio.repositories.YouTubeRepositoryImpl
import com.nvvi9.ytaudio.repositories.base.AudioInfoRepository
import com.nvvi9.ytaudio.repositories.base.SearchRepository
import com.nvvi9.ytaudio.repositories.base.YouTubeRepository
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@Module
@ExperimentalCoroutinesApi
@FlowPreview
abstract class RepositoryModule {

    @ExperimentalPagingApi
    @Binds
    abstract fun bindYouTubeRepository(youTubeRepositoryImpl: YouTubeRepositoryImpl): YouTubeRepository

    @Binds
    abstract fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindAudioInfoRepository(audioInfoRepositoryImpl: AudioInfoRepositoryImpl): AudioInfoRepository
}