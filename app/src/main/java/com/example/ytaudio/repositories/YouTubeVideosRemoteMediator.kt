package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.data.youtube.YTVideosRemoteKeys
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.db.YTVideosItemDao
import com.example.ytaudio.db.YTVideosRemoteKeysDao
import com.example.ytaudio.network.YouTubeApiService
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class YouTubeVideosRemoteMediator @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val database: AudioDatabase,
    private val ytVideosRemoteKeysDao: YTVideosRemoteKeysDao,
    private val ytVideosItemDao: YTVideosItemDao
) : RemoteMediator<Int, YTVideosItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, YTVideosItem>
    ): MediatorResult {
        val pageToken = when (loadType) {
            LoadType.APPEND -> {
                val remoteKeys = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                    ?.let { ytVideosRemoteKeysDao.remoteKeysById(it.id) }

                if (remoteKeys?.nextPageToken == null) {
                    return MediatorResult.Success(true)
                }

                remoteKeys.nextPageToken
            }
            LoadType.PREPEND -> {
                val remoteKeys =
                    state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                        ?.let { ytVideosRemoteKeysDao.remoteKeysById(it.id) }
                        ?: throw InvalidObjectException("remote key and prevPageToken should not be null")

                remoteKeys.prevPageToken ?: return MediatorResult.Success(true)
            }
            LoadType.REFRESH -> {
                val remoteKeys = state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.id?.let { id ->
                        ytVideosRemoteKeysDao.remoteKeysById(id)
                    }
                }

                remoteKeys?.nextPageToken
            }
        }

        return try {
            val ytVideosResponse =
                ytApiService.getYTVideosResponse(state.config.pageSize, pageToken)
            val items = ytVideosResponse.items

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    ytVideosRemoteKeysDao.clear()
                    ytVideosItemDao.clear()
                }

                val keys = items.map {
                    YTVideosRemoteKeys(
                        it.id, ytVideosResponse.prevPageToken,
                        ytVideosResponse.nextPageToken
                    )
                }

                ytVideosRemoteKeysDao.insert(keys)
                ytVideosItemDao.insert(items)
            }

            MediatorResult.Success(items.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}