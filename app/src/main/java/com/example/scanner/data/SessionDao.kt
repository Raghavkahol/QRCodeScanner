package com.example.scanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(sessionData: SessionData) : Completable

    @Query("SELECT * FROM SessionData")
    fun getSessionData(): Single<List<SessionData>>

    @Query("DELETE FROM SessionData")
    fun deleteSessionData(): Completable
}