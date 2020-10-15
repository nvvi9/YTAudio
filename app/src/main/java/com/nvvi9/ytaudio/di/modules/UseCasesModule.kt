package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.domain.AudioInfoUseCase
import com.nvvi9.ytaudio.domain.SearchUseCase
import com.nvvi9.ytaudio.domain.YouTubeUseCase
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
@Module
abstract class UseCasesModule {

    @ContributesAndroidInjector
    abstract fun contributeAudioInfoUseCase(): AudioInfoUseCase

    @ContributesAndroidInjector
    @ExperimentalPagingApi
    abstract fun contributeYouTubeUseCase(): YouTubeUseCase

    @ContributesAndroidInjector
    abstract fun contributeSearchUseCase(): SearchUseCase
}