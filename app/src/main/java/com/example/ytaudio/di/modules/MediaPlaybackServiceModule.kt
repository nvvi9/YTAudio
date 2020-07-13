package com.example.ytaudio.di.modules

import com.example.ytaudio.service.MediaPlaybackService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MediaPlaybackServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeMediaPlaybackService(): MediaPlaybackService
}