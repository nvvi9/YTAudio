package com.example.ytaudio.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.ytaudio.data.youtube.YTVideosItem


@Dao
interface YTVideosItemDao : BaseDao<YTVideosItem> {

    @Query("SELECT * FROM YTVideosItem")
    fun allItems(): PagingSource<Int, YTVideosItem>

    @Query("DELETE FROM YTVideosItem")
    suspend fun clear()
}