package com.example.ytaudio.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AudioDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audio: AudioInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audioList: List<AudioInfo>)

    @Update
    suspend fun update(audio: AudioInfo)

    @Update
    suspend fun update(audioList: List<AudioInfo>)

    @Query("SELECT * FROM audio_playlist_table WHERE youtube_id = :id")
    suspend fun get(id:String): AudioInfo?

    @Query("SELECT * FROM audio_playlist_table ORDER BY last_update_time_seconds")
    suspend fun getAllAudioInfo(): List<AudioInfo>

    @Query("SELECT * FROM audio_playlist_table ORDER BY last_update_time_seconds")
    fun getAllAudio(): LiveData<List<AudioInfo>?>

    @Delete
    suspend fun delete(audio: AudioInfo)

    @Query("DELETE FROM audio_playlist_table WHERE youtube_id IN (:idList)")
    suspend fun delete(idList: List<String>)

    @Query("DELETE FROM audio_playlist_table")
    suspend fun clear()
}