package com.nvvi9.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.db.YTVideoDetailsDao
import com.nvvi9.ytaudio.network.YouTubeApiService
import com.nvvi9.ytstream.YTStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@Singleton
class YouTubeRepository @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytStream: YTStream,
    private val YTVideoDetailsDao: YTVideoDetailsDao,
    private val ytVideoDetailsRemoteMediator: YTVideoDetailsRemoteMediator
) : Repository {

    fun getVideosResponse(): Flow<PagingData<YTVideoDetails>> = Pager(
        config = PagingConfig(PAGE_SIZE),
        remoteMediator = ytVideoDetailsRemoteMediator,
        pagingSourceFactory = { YTVideoDetailsDao.allItems() }
    ).flow

    fun getSearchResponse(query: String): Flow<PagingData<YTVideoDetails>> =
        Pager(PagingConfig(PAGE_SIZE)) {
            YTSearchPagingSource(query, ytApiService, ytStream)
        }.flow

    companion object {
        private const val PAGE_SIZE = 10
    }
}