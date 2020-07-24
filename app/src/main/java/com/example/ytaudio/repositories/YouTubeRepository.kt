package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ytaudio.data.videodata.VideoData
import com.example.ytaudio.db.VideoDataDao
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.utils.Constants.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


interface YouTubeRepository {

    fun getVideosResponse(): Flow<PagingData<VideoData>>
    fun getSearchResponse(query: String): Flow<PagingData<VideoData>>
}


@ExperimentalPagingApi
@Singleton
class YouTubeRepositoryImpl @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytExtractor: YTExtractor,
    private val videoDataDao: VideoDataDao,
    private val ytVideoDataRemoteMediator: YTVideoDataRemoteMediator
) : YouTubeRepository {

    override fun getVideosResponse(): Flow<PagingData<VideoData>> = Pager(
        config = PagingConfig(PAGE_SIZE),
        remoteMediator = ytVideoDataRemoteMediator,
        pagingSourceFactory = { videoDataDao.allItems() }
    ).flow

    override fun getSearchResponse(query: String): Flow<PagingData<VideoData>> =
        Pager(PagingConfig(PAGE_SIZE)) { YTSearchPagingSource(query, ytApiService, ytExtractor) }
            .flow
}