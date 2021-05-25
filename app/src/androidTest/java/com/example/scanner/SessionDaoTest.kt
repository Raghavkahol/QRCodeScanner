package com.example.scanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.scanner.api.AppDatabase
import com.example.scanner.data.SessionDao
import com.example.scanner.data.SessionData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named
import org.junit.Assert.assertTrue

@HiltAndroidTest
@SmallTest
class SessionDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase
    private lateinit var sessionDao: SessionDao

    @Before
    fun setup() {
        hiltRule.inject()
        sessionDao = database.getSessionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSessionData()  {
        val sessionData = SessionData(1,"","",0F,0L,0L,0)
        sessionDao.insertData(sessionData).blockingAwait()
        val sessionList = sessionDao.getSessionData().blockingGet()
        assertTrue(sessionList.contains(sessionData))
    }
}