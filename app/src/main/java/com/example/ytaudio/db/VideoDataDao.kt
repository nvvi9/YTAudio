package com.example.ytaudio.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.ytaudio.data.videodata.VideoData


@Dao
interface VideoDataDao : BaseDao<VideoData> {

    @Query("SELECT * FROM VideoData")
    fun allItems(): PagingSource<Int, VideoData>

    @Query("DELETE FROM VideoData")
    suspend fun clear()
}