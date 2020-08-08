package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.db.VideoDetailsDao
import com.example.ytaudio.network.YTStreamApiService
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.utils.Constants.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class YouTubeRepository @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytStreamApiService: YTStreamApiService,
    private val videoDetailsDao: VideoDetailsDao,
    private val ytVideoDetailsRemoteMediator: YTVideoDetailsRemoteMediator
) : Repository {

    fun getVideosResponse(): Flow<PagingData<VideoDetails>> = Pager(
        config = PagingConfig(5),
        remoteMediator = ytVideoDetailsRemoteMediator,
        pagingSourceFactory = { videoDetailsDao.allItems() }
    ).flow

    fun getSearchResponse(query: String): Flow<PagingData<VideoDetails>> =
        Pager(PagingConfig(PAGE_SIZE)) {
            YTSearchPagingSource(query, ytApiService, ytStreamApiService)
        }.flow
}