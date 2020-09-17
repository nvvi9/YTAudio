package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributePlaylistFragment(): PlaylistFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ExperimentalPagingApi
    @ContributesAndroidInjector
    abstract fun contributeYouTubeFragment(): YouTubeFragment

    @ExperimentalPagingApi
    @ContributesAndroidInjector
    abstract fun contributeSearchResultsFragment(): SearchResultsFragment

    @ContributesAndroidInjector
    abstract fun contributePlayFragment(): PlayerFragment
}