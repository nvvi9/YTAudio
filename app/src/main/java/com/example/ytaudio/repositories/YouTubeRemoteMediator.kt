package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ytaudio.data.youtube.YTRemoteKeys
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.db.YTRemoteKeysDao
import com.example.ytaudio.db.YTVideosItemDao
import com.example.ytaudio.network.YouTubeApiService
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class YouTubeRemoteMediator @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val database: AudioDatabase,
    private val ytRemoteKeysDao: YTRemoteKeysDao,
    private val ytVideosItemDao: YTVideosItemDao
) : RemoteMediator<Int, YTVideosItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, YTVideosItem>
    ): MediatorResult {
        val pageToken = when (loadType) {
            LoadType.APPEND -> {
                val remoteKeys = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                    ?.let { ytRemoteKeysDao.remoteKeysById(it.id) }

                if (remoteKeys?.nextPageToken == null) {
                    return MediatorResult.Success(true)
                }

                remoteKeys.nextPageToken
            }
            LoadType.PREPEND -> {
                val remoteKeys =
                    state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                        ?.let { ytRemoteKeysDao.remoteKeysById(it.id) }
                        ?: throw InvalidObjectException("remote key and prevPageToken should not be null")

                remoteKeys.prevPageToken ?: return MediatorResult.Success(true)
            }
            LoadType.REFRESH -> {
                val remoteKeys = state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.id?.let { id ->
                        ytRemoteKeysDao.remoteKeysById(id)
                    }
                }

                remoteKeys?.nextPageToken
            }
        }

        return try {
            val ytVideosResponse =
                ytApiService.getYTVideosResponse(state.config.pageSize, pageToken)
            val items = ytVideosResponse.items
            val endOfPagination = items.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    ytRemoteKeysDao.clear()
                    ytVideosItemDao.clear()
                }

                val keys = items.map {
                    YTRemoteKeys(
                        it.id, ytVideosResponse.prevPageToken,
                        ytVideosResponse.nextPageToken
                    )
                }

                ytRemoteKeysDao.insert(keys)
                ytVideosItemDao.insert(items)
            }

            MediatorResult.Success(endOfPagination)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}