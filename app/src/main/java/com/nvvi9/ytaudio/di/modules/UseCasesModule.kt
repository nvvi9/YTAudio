package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.domain.AudioInfoUseCases
import com.nvvi9.ytaudio.domain.UseCases
import com.nvvi9.ytaudio.domain.YouTubeUseCases
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
@Module
abstract class UseCasesModule {

    @Binds
    abstract fun bindPlaylistUseCases(audioInfoUseCases: AudioInfoUseCases): UseCases

    @Binds
    @ExperimentalPagingApi
    abstract fun bindYouTubesUseCases(youTubeUseCases: YouTubeUseCases): UseCases
}