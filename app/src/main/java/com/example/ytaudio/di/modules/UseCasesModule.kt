package com.example.ytaudio.di.modules

import com.example.ytaudio.domain.*
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class UseCasesModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistUseCases(playlistUseCasesImpl: PlaylistUseCasesImpl): PlaylistUseCases

    @Binds
    @Singleton
    abstract fun bindYouTubesUseCases(youTubeUseCasesImpl: YouTubeUseCasesImpl): YouTubeUseCases

    @Binds
    @Singleton
    abstract fun bindSearchUseCases(searchUseCasesImpl: SearchUseCasesImpl): SearchUseCases
}