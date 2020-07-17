package com.example.ytaudio.di.modules

import com.example.ytaudio.repositories.PlaylistRepository
import com.example.ytaudio.repositories.PlaylistRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(playlistRepositoryImpl: PlaylistRepositoryImpl): PlaylistRepository
}