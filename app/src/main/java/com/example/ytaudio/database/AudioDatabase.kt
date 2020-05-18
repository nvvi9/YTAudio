package com.example.ytaudio.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AudioInfo::class], version = 1, exportSchema = false)
abstract class AudioDatabase : RoomDatabase() {

    abstract val audioDatabaseDao: AudioDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: AudioDatabase? = null

        fun getInstance(context: Context): AudioDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AudioDatabase::class.java,
                        "audio_playlist_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }

}