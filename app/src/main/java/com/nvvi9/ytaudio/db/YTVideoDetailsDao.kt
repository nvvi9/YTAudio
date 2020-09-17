package com.nvvi9.ytaudio.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails

@Dao
interface YTVideoDetailsDao : BaseDao<YTVideoDetails> {

    @Query("SELECT * FROM YTVideoDetails")
    fun allItems(): PagingSource<Int, YTVideoDetails>

    @Query("DELETE FROM YTVideoDetails")
    suspend fun clear()
}