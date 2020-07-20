package com.example.ytaudio.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ytaudio.data.youtube.YTVideosResponse


@Dao
interface YTVideosResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: YTVideosResponse)

    @Query("SELECT * FROM YTVideosResponse WHERE etag = :tag")
    fun getItemsByTag(tag: String): PagingSource<Int, YTVideosResponse>

    @Query("DELETE FROM YTVideosResponse WHERE etag = :tag")
    suspend fun deleteByTag(tag: String)
}