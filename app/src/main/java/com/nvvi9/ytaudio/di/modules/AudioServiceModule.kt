package com.nvvi9.ytaudio.di.modules

import com.nvvi9.ytaudio.service.AudioService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AudioServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeMediaPlaybackService(): AudioService
}