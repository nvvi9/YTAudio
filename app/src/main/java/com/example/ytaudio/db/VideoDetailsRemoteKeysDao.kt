package com.example.ytaudio.db

import androidx.room.Dao
import androidx.room.Query
import com.example.ytaudio.data.streamyt.VideoDetailsRemoteKeys


@Dao
interface VideoDetailsRemoteKeysDao : BaseDao<VideoDetailsRemoteKeys> {

    @Query("SELECT * FROM VideoDetailsRemoteKeys WHERE id = :id")
    suspend fun remoteKeysById(id: String): VideoDetailsRemoteKeys

    @Query("DELETE FROM VideoDetailsRemoteKeys WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM VideoDetailsRemoteKeys")
    suspend fun clear()
}