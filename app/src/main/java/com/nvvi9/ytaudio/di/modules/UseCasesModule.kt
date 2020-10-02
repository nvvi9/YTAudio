package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.domain.SearchUseCases
import com.nvvi9.ytaudio.domain.UseCases
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@Module
abstract class UseCasesModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistUseCases(audioInfoUseCases: AudioInfoUseCases): UseCases

    @ExperimentalCoroutinesApi
    @FlowPreview
    @Binds
    @Singleton
    @ExperimentalPagingApi
    abstract fun bindYouTubesUseCases(youTubeUseCases: YouTubeUseCases): UseCases

    @Binds
    @Singleton
    abstract fun bindSearchUseCases(searchUseCases: SearchUseCases): UseCases
}