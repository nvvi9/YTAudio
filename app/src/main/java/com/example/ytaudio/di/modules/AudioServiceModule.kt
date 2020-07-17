package com.example.ytaudio.di.modules

import com.example.ytaudio.service.AudioService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AudioServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeMediaPlaybackService(): AudioService
}