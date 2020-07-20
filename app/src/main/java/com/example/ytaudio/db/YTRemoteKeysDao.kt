package com.example.ytaudio.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ytaudio.data.youtube.YTRemoteKeys


@Dao
interface YTRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: YTRemoteKeys)

    @Query("SELECT * FROM YTRemoteKeys WHERE etag = :tag")
    suspend fun remoteKeyByTag(tag: String): YTRemoteKeys

    @Query("DELETE FROM YTRemoteKeys WHERE etag = :tag")
    suspend fun deleteByTag(tag: String)
}