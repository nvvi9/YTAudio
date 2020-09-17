package com.nvvi9.ytaudio.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy


interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<T>)
}