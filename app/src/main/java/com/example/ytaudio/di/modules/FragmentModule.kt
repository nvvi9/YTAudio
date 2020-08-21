package com.example.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.example.ytaudio.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributePlaylistFragment(): PlaylistFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributePlayerFragment(): PlayerFragment

    @ExperimentalPagingApi
    @ContributesAndroidInjector
    abstract fun contributeYouTubeFragment(): YouTubeFragment

    @ExperimentalPagingApi
    @ContributesAndroidInjector
    abstract fun contributeSearchResultsFragment(): SearchResultsFragment

    @ContributesAndroidInjector
    abstract fun contributePlayFragment(): PlayFragment
}