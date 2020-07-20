package com.example.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.ytaudio.data.youtube.YTRemoteKeys
import com.example.ytaudio.data.youtube.YTVideosResponse
import com.example.ytaudio.db.AudioDatabase
import com.example.ytaudio.db.YTRemoteKeysDao
import com.example.ytaudio.db.YTVideosResponseDao
import com.example.ytaudio.network.YouTubeApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@ExperimentalPagingApi
class YouTubeRemoteMediator(private val etag: String) : RemoteMediator<String, YTVideosResponse>() {

    @Inject
    lateinit var ytApiService: YouTubeApiService

    @Inject
    lateinit var database: AudioDatabase

    @Inject
    lateinit var ytRemoteKeysDao: YTRemoteKeysDao

    @Inject
    lateinit var ytVideosResponseDao: YTVideosResponseDao


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<String, YTVideosResponse>
    ): MediatorResult {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> {
                    val remoteKey = database.withTransaction {
                        ytRemoteKeysDao.remoteKeyByTag(etag)
                    }

                    if (remoteKey.nextPageToken == null) {
                        return MediatorResult.Success(true)
                    }

                    remoteKey.nextPageToken
                }
            }

            val response = ytApiService.getYTVideosResponseAsync(loadKey)

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    ytVideosResponseDao.deleteByTag(etag)
                    ytRemoteKeysDao.deleteByTag(etag)
                }

                ytRemoteKeysDao.insert(YTRemoteKeys(etag, response.nextPageToken))
                ytVideosResponseDao.insert(response)
            }
            return MediatorResult.Success(response.items.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}