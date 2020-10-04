package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.repositories.AudioInfoRepository
import com.nvvi9.ytaudio.repositories.SearchRepository
import com.nvvi9.ytaudio.repositories.YouTubeRepository
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@Module
@ExperimentalCoroutinesApi
@FlowPreview
abstract class RepositoryModule {

    @ExperimentalPagingApi
    @ContributesAndroidInjector
    abstract fun contributeYouTubeRepository(): YouTubeRepository

    @ContributesAndroidInjector
    abstract fun contributeSearchRepository(): SearchRepository

    @ContributesAndroidInjector
    abstract fun contributeAudioInfoRepository(): AudioInfoRepository
}