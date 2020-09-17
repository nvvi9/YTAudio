package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.domain.PlaylistUseCases
import com.nvvi9.ytaudio.domain.SearchUseCases
import com.nvvi9.ytaudio.domain.UseCases
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class UseCasesModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistUseCases(playlistUseCases: PlaylistUseCases): UseCases

    @Binds
    @Singleton
    @ExperimentalPagingApi
    abstract fun bindYouTubesUseCases(youTubeUseCases: YouTubeUseCases): UseCases

    @Binds
    @Singleton
    abstract fun bindSearchUseCases(searchUseCases: SearchUseCases): UseCases
}