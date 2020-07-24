package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.db.YTVideosItemDao
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.utils.Constants.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


interface YouTubeRepository {

    fun getVideosResponse(): Flow<PagingData<YTVideosItem>>
    fun getSearchResponse(query: String): Flow<PagingData<AudioInfo>>
}


@ExperimentalPagingApi
@Singleton
class YouTubeRepositoryImpl @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytExtractor: YTExtractor,
    private val ytVideosItemDao: YTVideosItemDao,
    private val ytVideosRemoteMediator: YouTubeVideosRemoteMediator
) : YouTubeRepository {

    override fun getVideosResponse(): Flow<PagingData<YTVideosItem>> = Pager(
        config = PagingConfig(PAGE_SIZE),
        remoteMediator = ytVideosRemoteMediator,
        pagingSourceFactory = { ytVideosItemDao.allItems() }
    ).flow

    override fun getSearchResponse(query: String): Flow<PagingData<AudioInfo>> =
        Pager(PagingConfig(PAGE_SIZE)) { YTSearchPagingSource(query, ytApiService, ytExtractor) }
            .flow
}