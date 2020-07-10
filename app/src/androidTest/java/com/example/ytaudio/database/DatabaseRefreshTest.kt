package com.example.ytaudio.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ytaudio.repositories.AudioRepository
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DatabaseRefreshTest {

    private lateinit var audioRepository: AudioRepository
    private lateinit var database: AudioDatabase
    private lateinit var databaseDao: AudioDatabaseDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        audioRepository = AudioRepository(context)
        database = AudioDatabase.getInstance(context)
        databaseDao = database.audioDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun fullRefresh() {
        runBlocking {
            val oldAudioInfo = databaseDao.getAllAudioInfo()
            audioRepository.updateAllAudioInfo()
            val updatedAudioInfo = databaseDao.getAllAudioInfo()
            assertThat(updatedAudioInfo.size, `is`(oldAudioInfo.size))
        }
    }
}