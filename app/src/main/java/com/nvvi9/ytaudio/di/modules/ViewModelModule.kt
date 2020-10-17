package com.nvvi9.ytaudio.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.di.factories.ViewModelFactory
import com.nvvi9.ytaudio.di.keys.ViewModelKey
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import com.nvvi9.ytaudio.ui.viewmodels.PlaylistViewModel
import com.nvvi9.ytaudio.ui.viewmodels.SearchViewModel
import com.nvvi9.ytaudio.ui.viewmodels.YouTubeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@Module
@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistViewModel::class)
    abstract fun bindPlaylistViewModel(playlistViewModel: PlaylistViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun bindPlayerViewModel(playerViewModel: PlayerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(YouTubeViewModel::class)
    abstract fun bindYouTubeViewModel(youTubeViewModel: YouTubeViewModel): ViewModel

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}