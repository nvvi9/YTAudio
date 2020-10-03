package com.nvvi9.ytaudio.di.modules

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.repositories.YTVideoDetailsPagingSource
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@Module
@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalPagingApi
abstract class PagingModule {

    @Binds
    @Singleton
    abstract fun bindYTVideoDetailsPagingSource(ytVideoDetailsPagingSource: YTVideoDetailsPagingSource): PagingSource<String, YTVideoDetails>
}