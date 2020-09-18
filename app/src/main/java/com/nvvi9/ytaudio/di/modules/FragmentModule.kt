package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@Module
@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributePlaylistFragment(): PlaylistFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeYouTubeFragment(): YouTubeFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchResultsFragment(): SearchResultsFragment

    @ContributesAndroidInjector
    abstract fun contributePlayFragment(): PlayerFragment
}