package com.nvvi9.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.network.YTStreamDataSource
import com.nvvi9.ytaudio.network.YouTubeNetworkDataSource
import com.nvvi9.ytaudio.repositories.base.YouTubeRepository
import com.nvvi9.ytaudio.repositories.paging.YTSearchPagingSource
import com.nvvi9.ytaudio.repositories.paging.YTVideoDetailsPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class YouTubeRepositoryImpl @Inject constructor(
    private val ytNetworkDataSource: YouTubeNetworkDataSource,
    private val ytStreamDataSource: YTStreamDataSource
) : YouTubeRepository {

    override fun getVideoDetailsFromQuery(query: String): Flow<PagingData<YTVideoDetails>> =
        Pager(PagingConfig(PAGE_SIZE)) {
            YTSearchPagingSource(query, ytNetworkDataSource, ytStreamDataSource)
        }.flow


    override fun getVideoDetails(): Flow<PagingData<YTVideoDetails>> =
        Pager(PagingConfig(PAGE_SIZE)) {
            YTVideoDetailsPagingSource(ytNetworkDataSource, ytStreamDataSource)
        }.flow

    companion object {
        private const val PAGE_SIZE = 4
    }
}