package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
@Module
abstract class UseCasesModule {

    @ContributesAndroidInjector
    abstract fun contributeAudioInfoUseCases(): AudioInfoUseCases

    @ContributesAndroidInjector
    @ExperimentalPagingApi
    abstract fun contributeYouTubeUseCases(): YouTubeUseCases
}