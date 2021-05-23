package com.example.scanner.home

import com.example.scanner.api.ScannerService
import com.example.scanner.data.SessionDao
import com.example.scanner.data.SessionData
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class HomeRepository @Inject constructor(private val scannerService : ScannerService ,private val sessionDao: SessionDao)  {
    fun postSessionData(sessionData: SessionData) : Completable {
       // scannerService.
        return Completable.complete()
    }

    fun insertSessionData(sessionData: SessionData) : Completable{
          return  sessionDao.insertData(sessionData)
    }

    fun getSessionData(): Single<List<SessionData>> {
        return sessionDao.getSessionData()
    }

    fun deleteSessionData() : Completable {
        return sessionDao.deleteSessionData()
    }
}