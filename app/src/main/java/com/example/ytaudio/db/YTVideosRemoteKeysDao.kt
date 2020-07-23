package com.example.ytaudio.db

import androidx.room.Dao
import androidx.room.Query
import com.example.ytaudio.data.youtube.YTVideosRemoteKeys


@Dao
interface YTVideosRemoteKeysDao : BaseDao<YTVideosRemoteKeys> {

    @Query("SELECT * FROM YTVideosRemoteKeys WHERE id = :id")
    suspend fun remoteKeysById(id: String): YTVideosRemoteKeys?

    @Query("DELETE FROM YTVideosRemoteKeys WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM YTVideosRemoteKeys")
    suspend fun clear()
}