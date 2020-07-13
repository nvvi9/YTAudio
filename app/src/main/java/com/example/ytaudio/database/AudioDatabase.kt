package com.example.ytaudio.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ytaudio.database.entities.AudioInfo

@Database(entities = [AudioInfo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val audioDatabaseDao: AudioDatabaseDao
}