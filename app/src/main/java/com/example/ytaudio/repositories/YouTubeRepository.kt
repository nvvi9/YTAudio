package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.db.YTVideosItemDao
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.utils.Constants.PAGE_SIZE
import com.example.ytaudio.vo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


interface YouTubeRepository {

    fun getVideosResponse(): Flow<PagingData<YTVideosItem>>
    suspend fun getSearchResponse(query: String): Result<YTSearchResponse>
}


@ExperimentalPagingApi
@Singleton
class YouTubeRepositoryImpl
@Inject constructor(
    private val ytVideosItemDao: YTVideosItemDao,
    private val ytApiService: YouTubeApiService,
    private val ytVideosRemoteMediator: YouTubeVideosRemoteMediator
) : YouTubeRepository {

    override fun getVideosResponse(): Flow<PagingData<YTVideosItem>> {
        val pagingSourceFactory =
            { ytVideosItemDao.allItems() }

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            remoteMediator = ytVideosRemoteMediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getSearchResponse(query: String): Result<YTSearchResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = ytApiService.getYTSearchResponse(query)
                Result.Success(response)
            } catch (t: Throwable) {
                Result.Error<YTSearchResponse>(t)
            }
        }
}

