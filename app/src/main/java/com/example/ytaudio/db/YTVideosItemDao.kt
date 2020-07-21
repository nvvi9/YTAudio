package com.example.ytaudio.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ytaudio.data.youtube.YTVideosItem


@Dao
interface YTVideosItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<YTVideosItem>)

    @Query("SELECT * FROM YTVideosItem WHERE categoryId = :id")
    fun itemsByCategoryId(id: String): PagingSource<Int, YTVideosItem>

    @Query("DELETE FROM YTVideosItem")
    suspend fun clear()
}