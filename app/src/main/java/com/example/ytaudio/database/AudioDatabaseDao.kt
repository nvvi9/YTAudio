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

    @Query("SELECT * FROM audio_playlist_table WHERE audioId = :key")
    suspend fun get(key: Long): AudioInfo?

    @Query("SELECT * FROM audio_playlist_table ORDER BY audioId DESC")
    suspend fun getAllAudioInfo(): List<AudioInfo>

    @Query("SELECT * FROM audio_playlist_table ORDER BY audioId DESC")
    fun getAllAudio(): LiveData<List<AudioInfo>?>

    @Query("SELECT * FROM audio_playlist_table ORDER BY audioId DESC LIMIT 1")
    fun getLastAudio(): AudioInfo?

    @Delete
    suspend fun delete(audio: AudioInfo)

    @Query("DELETE FROM audio_playlist_table WHERE audioId IN (:idList)")
    suspend fun delete(idList: List<Long>)

    @Query("DELETE FROM audio_playlist_table")
    suspend fun clear()
}