package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ytaudio.data.videodata.VideoData
import com.example.ytaudio.data.videodata.VideoDataRemoteKeys
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.db.VideoDataDao
import com.example.ytaudio.db.VideoDataRemoteKeysDao
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class YTVideoDataRemoteMediator @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytExtractor: YTExtractor,
    private val database: AudioDatabase,
    private val videoDataRemoteKeysDao: VideoDataRemoteKeysDao,
    private val videoDataDao: VideoDataDao
) : RemoteMediator<Int, VideoData>() {

    @FlowPreview
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VideoData>
    ): MediatorResult {
        val pageToken = when (loadType) {
            LoadType.APPEND -> state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { videoDataRemoteKeysDao.remoteKeysById(it.id) }?.nextPageToken
                ?: return MediatorResult.Success(true)
            LoadType.PREPEND -> state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { videoDataRemoteKeysDao.remoteKeysById(it.id) }?.prevPageToken
                ?: return MediatorResult.Success(true)
            LoadType.REFRESH -> state.anchorPosition?.let {
                state.closestItemToPosition(it)?.id
                    ?.let { id -> videoDataRemoteKeysDao.remoteKeysById(id) }?.nextPageToken
            }
        }

        return try {
            val ytResponse = ytApiService.getYTVideosIdResponse(state.config.pageSize, pageToken)
            val responseItems = ytResponse.items

            val dataItems = responseItems
                .map { it.id }.asFlow()
                .flatMapMerge(responseItems.size) { ytExtractor.extractVideoDataFlow(it) }
                .filterNotNull()
                .flowOn(Dispatchers.IO)
                .toList()

            val keys = dataItems.map {
                VideoDataRemoteKeys(it.id, ytResponse.prevPageToken, ytResponse.nextPageToken)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    videoDataRemoteKeysDao.clear()
                    videoDataDao.clear()
                }

                videoDataRemoteKeysDao.insert(keys)
                videoDataDao.insert(dataItems)
            }

            MediatorResult.Success(dataItems.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}