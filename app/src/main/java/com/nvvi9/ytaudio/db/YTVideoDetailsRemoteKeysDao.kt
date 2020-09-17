package com.nvvi9.ytaudio.db

import androidx.room.Dao
import androidx.room.Query
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetailsRemoteKeys


@Dao
interface YTVideoDetailsRemoteKeysDao : BaseDao<YTVideoDetailsRemoteKeys> {

    @Query("SELECT * FROM YTVideoDetailsRemoteKeys WHERE id = :id")
    suspend fun remoteKeysById(id: String): YTVideoDetailsRemoteKeys

    @Query("DELETE FROM YTVideoDetailsRemoteKeys WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM YTVideoDetailsRemoteKeys")
    suspend fun clear()
}