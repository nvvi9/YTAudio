package com.nvvi9.ytaudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo


@Database(entities = [AudioInfo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val audioInfoDao: AudioInfoDao
}