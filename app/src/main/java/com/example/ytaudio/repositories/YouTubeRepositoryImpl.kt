package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.db.YTVideosItemDao
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.repositories.base.YouTubeRepository
import com.example.ytaudio.vo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class YouTubeRepositoryImpl
@Inject constructor(
    private val ytVideosItemDao: YTVideosItemDao,
    private val ytApiService: YouTubeApiService,
    private val ytRemoteMediator: YouTubeRemoteMediator
) : YouTubeRepository {

    override fun getVideosResponse(): Flow<PagingData<YTVideosItem>> {
        val pagingSourceFactory =
            { ytVideosItemDao.itemsByCategoryId(YouTubeRepository.MUSIC_CATEGORY_ID) }

        return Pager(
            config = PagingConfig(pageSize = YouTubeRepository.PAGE_SIZE),
            remoteMediator = ytRemoteMediator,
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