package com.example.ytaudio.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ytaudio.data.youtube.YTRemoteKeys


@Dao
interface YTRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<YTRemoteKeys>)

    @Query("SELECT * FROM YTRemoteKeys WHERE id = :id")
    suspend fun remoteKeysById(id: String): YTRemoteKeys?

    @Query("DELETE FROM YTRemoteKeys WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM YTRemoteKeys")
    suspend fun clear()
}