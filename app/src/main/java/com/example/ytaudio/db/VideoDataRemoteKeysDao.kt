package com.example.ytaudio.db

import androidx.room.Dao
import androidx.room.Query
import com.example.ytaudio.data.videodata.VideoDataRemoteKeys


@Dao
interface VideoDataRemoteKeysDao : BaseDao<VideoDataRemoteKeys> {

    @Query("SELECT * FROM VideoDataRemoteKeys WHERE id = :id")
    suspend fun remoteKeysById(id: String): VideoDataRemoteKeys

    @Query("DELETE FROM VideoDataRemoteKeys WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM VideoDataRemoteKeys")
    suspend fun clear()
}