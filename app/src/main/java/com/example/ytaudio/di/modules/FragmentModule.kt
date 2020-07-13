package com.example.ytaudio.di.modules

import com.example.ytaudio.player.PlayerFragment
import com.example.ytaudio.playlist.PlaylistFragment
import com.example.ytaudio.search.SearchFragment
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
}