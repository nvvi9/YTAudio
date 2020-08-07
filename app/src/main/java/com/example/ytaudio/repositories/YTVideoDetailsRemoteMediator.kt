package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.data.streamyt.VideoDetailsRemoteKeys
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.db.VideoDetailsDao
import com.example.ytaudio.db.VideoDetailsRemoteKeysDao
import com.example.ytaudio.network.YTStreamApiService
import com.example.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.FlowPreview
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class YTVideoDetailsRemoteMediator @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytStreamApiService: YTStreamApiService,
    private val database: AudioDatabase,
    private val videoDetailsDao: VideoDetailsDao,
    private val videoDetailsRemoteKeysDao: VideoDetailsRemoteKeysDao
) : RemoteMediator<Int, VideoDetails>() {

    @FlowPreview
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VideoDetails>
    ): MediatorResult {
        val pageToken = when (loadType) {
            LoadType.APPEND -> state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { videoDetailsRemoteKeysDao.remoteKeysById(it.id) }?.nextPageToken
                ?: return MediatorResult.Success(true)
            LoadType.PREPEND -> state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { videoDetailsRemoteKeysDao.remoteKeysById(it.id) }?.prevPageToken
                ?: return MediatorResult.Success(true)
            LoadType.REFRESH -> state.anchorPosition?.let {
                state.closestItemToPosition(it)?.id
                    ?.let { id -> videoDetailsRemoteKeysDao.remoteKeysById(id) }?.nextPageToken
            }
        }

        return try {
            val ytResponse = ytApiService.getYTVideosIdResponse(state.config.pageSize, pageToken)
            val responseItems = ytResponse.items

            val items =
                ytStreamApiService.getVideoDetails(responseItems.joinToString("+") { it.id })

            val keys = items.map {
                VideoDetailsRemoteKeys(it.id, ytResponse.prevPageToken, ytResponse.nextPageToken)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    videoDetailsRemoteKeysDao.clear()
                    videoDetailsDao.clear()
                }

                videoDetailsRemoteKeysDao.insert(keys)
                videoDetailsDao.insert(items)
            }

            MediatorResult.Success(items.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}