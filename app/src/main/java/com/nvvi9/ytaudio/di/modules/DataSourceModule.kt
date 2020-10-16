package com.nvvi9.ytaudio.di.modules

import com.nvvi9.ytaudio.network.AutoCompleteNetworkDataSource
import com.nvvi9.ytaudio.network.YouTubeNetworkDataSource
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class DataSourceModule {

    @ContributesAndroidInjector
    abstract fun contributeYouTubeNetworkDataSource(): YouTubeNetworkDataSource

    @ContributesAndroidInjector
    abstract fun contributeAutoCompleteNetworkDataSource(): AutoCompleteNetworkDataSource
}