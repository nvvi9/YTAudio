package com.example.ytaudio.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.ytaudio.data.streamyt.VideoDetails

@Dao
interface VideoDetailsDao : BaseDao<VideoDetails> {

    @Query("SELECT * FROM VideoDetails")
    fun allItems(): PagingSource<Int, VideoDetails>

    @Query("DELETE FROM VideoDetails")
    suspend fun clear()
}