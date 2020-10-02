package com.nvvi9.ytaudio.di.modules

import com.nvvi9.ytaudio.service.AudioService
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@Module
abstract class AudioServiceModule {

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ContributesAndroidInjector
    abstract fun contributeMediaPlaybackService(): AudioService
}