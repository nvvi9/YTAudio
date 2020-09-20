package com.nvvi9.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetailsRemoteKeys
import com.nvvi9.ytaudio.db.AudioDatabase
import com.nvvi9.ytaudio.db.YTVideoDetailsDao
import com.nvvi9.ytaudio.db.YTVideoDetailsRemoteKeysDao
import com.nvvi9.ytaudio.network.YouTubeApiService
import com.nvvi9.ytstream.YTStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@FlowPreview
@ExperimentalPagingApi
@ExperimentalCoroutinesApi
class YTVideoDetailsRemoteMediator @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytStream: YTStream,
    private val database: AudioDatabase,
    private val YTVideoDetailsDao: YTVideoDetailsDao,
    private val YTVideoDetailsRemoteKeysDao: YTVideoDetailsRemoteKeysDao
) : RemoteMediator<Int, YTVideoDetails>() {

    @FlowPreview
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, YTVideoDetails>
    ): MediatorResult {
        val pageToken = when (loadType) {
            LoadType.APPEND -> state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { YTVideoDetailsRemoteKeysDao.remoteKeysById(it.id) }?.nextPageToken
                ?: return MediatorResult.Success(true)
            LoadType.PREPEND -> state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { YTVideoDetailsRemoteKeysDao.remoteKeysById(it.id) }?.prevPageToken
                ?: return MediatorResult.Success(true)
            LoadType.REFRESH -> state.anchorPosition?.let {
                state.closestItemToPosition(it)?.id
                    ?.let { id -> YTVideoDetailsRemoteKeysDao.remoteKeysById(id) }?.nextPageToken
            }
        }

        return try {
            val ytResponse = ytApiService.getYTVideosIdResponse(state.config.pageSize, pageToken)
            val responseItems = ytResponse.items

            val items =
                ytStream.extractVideoDetails(*responseItems.map { it.id }.toTypedArray())
                    .toList()
                    .filterNotNull()
                    .map { YTVideoDetails.create(it) }

            val keys = items.map {
                YTVideoDetailsRemoteKeys(it.id, ytResponse.prevPageToken, ytResponse.nextPageToken)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    YTVideoDetailsRemoteKeysDao.clear()
                    YTVideoDetailsDao.clear()
                }

                YTVideoDetailsRemoteKeysDao.insert(keys)
                YTVideoDetailsDao.insert(items)
            }

            MediatorResult.Success(items.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}