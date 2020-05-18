package com.example.ytaudio

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class AudioDatabaseTest {

    private lateinit var audioDao: AudioDatabaseDao
    private lateinit var db: AudioDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AudioDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        audioDao = db.audioDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGet() {
        val audio = AudioInfo(audioUri = "string")
        audioDao.insert(audio)
        val lastAdded = audioDao.getLastAudio()
        assertEquals(lastAdded?.audioUri, "string")
    }
}