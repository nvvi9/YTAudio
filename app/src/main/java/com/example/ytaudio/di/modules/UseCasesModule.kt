package com.example.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.example.ytaudio.domain.PlaylistUseCases
import com.example.ytaudio.domain.SearchUseCases
import com.example.ytaudio.domain.UseCases
import com.example.ytaudio.domain.YouTubeUseCases
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