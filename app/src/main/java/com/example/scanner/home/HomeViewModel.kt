package com.example.scanner.home

import android.annotation.SuppressLint
import android.os.Handler
import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scanner.BaseViewModel
import com.example.scanner.data.SessionData
import com.example.scanner.data.SessionInfoRequest
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeViewModel @AssistedInject constructor(private val homeRepository: HomeRepository,
                                                 @Assisted private val sessionData: String) : BaseViewModel() {

     var isScanStartState = MutableLiveData<Boolean>()
     var isSessionInProgressState = MutableLiveData<Boolean>()
     var isSessionCompletedState = MutableLiveData<Boolean>()
     var sesseionStartTime =  MutableLiveData<String>()
     var locationId =  MutableLiveData<String>()
     var locationDetail =  MutableLiveData<String>()
     var pricePerMin =  MutableLiveData<String>()
     var sessionPrice =  MutableLiveData<String>()
     var endTime =  MutableLiveData<String>()
     var sessionTimer = MutableLiveData<String>()
     private val mHandler: Handler = Handler()
     private var startTime : Long = 0L
    
    init {
        getSessionData()
    }

    private fun getSessionData() {
        bindDisposable {
            homeRepository.getSessionData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setUpSessionState(it)
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun setUpSessionState(sessionList : List<SessionData>) {
        if(sessionList.isNullOrEmpty()) {
            initializeNonPersistedState()
        } else {
            initializePersistedState(sessionList)
        }
    }

    private fun initializeNonPersistedState() {
        if(sessionData.isNotEmpty()) {
            handleInProgressState()
        } else {
            setScanNowState()
        }
    }

    private fun initializePersistedState(sessionList : List<SessionData>) {
        val persistedSessionData = sessionList[0]
        if(isSessionInCompletedState(persistedSessionData)){
            handleCompletedState(persistedSessionData)
        } else {
            handlePersistedProgressState(persistedSessionData)
        }
    }

    private fun handleInProgressState() {
        val session = parseSessionData(sessionData)
        setSessionInProgressState()
        session.start_time = System.currentTimeMillis()
        startTime = session.start_time
        insertSessionStartData(session)
        startTimerTask();
    }

    private fun parseSessionData(sessionData: String) : SessionData {
        var ss = sessionData.substring(1, sessionData.length - 1)
        ss = ss.replace("\\", "")
        return Gson().fromJson(ss, SessionData::class.java)
    }

    private fun insertSessionStartData(persistedSessionData: SessionData) {
        if(persistedSessionData.start_time == 0L)
            persistedSessionData.start_time = System.currentTimeMillis()
        bindDisposable {
            homeRepository.insertSessionData(persistedSessionData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleSessionStartState(persistedSessionData)
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun startTimerTask() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60
            if (seconds < 10) {
                sessionTimer.value = "$minutes:0$seconds"
            } else {
                sessionTimer.value = "$minutes:$seconds"
            }
           mHandler.postDelayed(this,  1000)
        }
    }
    private fun setSessionInProgressState() {
        isScanStartState.value = false
        isSessionCompletedState.value = false
        isSessionInProgressState.value = true
    }

    private fun setScanNowState() {
        isSessionInProgressState.value = false
        isSessionCompletedState.value = false
        isScanStartState.value = true
    }

    private fun isSessionInCompletedState(persistedSessionData: SessionData) : Boolean{
        return sessionData.isNotEmpty() && persistedSessionData.start_time != 0L || persistedSessionData.end_time != 0L
    }

    private fun handleCompletedState(persistedSessionData: SessionData) {
        setSessionCompletedState()
        insertSessionEndData(persistedSessionData)
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private fun setSessionCompletedState() {
        isScanStartState.value = false
        isSessionInProgressState.value = false
        isSessionCompletedState.value = true
    }

    private fun insertSessionEndData(persistedSessionData: SessionData) {
        persistedSessionData.end_time = System.currentTimeMillis()
        bindDisposable {
            homeRepository.insertSessionData(persistedSessionData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleSessionEndState(persistedSessionData)
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun handlePersistedProgressState(persistedSessionData: SessionData) {
        startTime = persistedSessionData.start_time
        startTimerTask()
        setSessionInProgressState()
        insertSessionStartData(persistedSessionData)
    }

    private fun handleSessionStartState(sessionData: SessionData) {
        sessionData.run {
            sesseionStartTime.value =  getSessionTimeFormat().format(Date(sessionData.start_time))
            locationId.value = location_id
            locationDetail.value = location_details
            pricePerMin.value = price_per_min.toString()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getSessionTimeFormat(): DateFormat {
        return SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    }

    private fun handleSessionEndState(persistedSessionData: SessionData) {
        persistedSessionData.run {
            setSessionDataUI(this)
            val minutesSpent = calculateSessionPrice(start_time, end_time)
            val price: Float? = price_per_min?.let{(minutesSpent?.toFloat()?.times(it))}
            sessionPrice.value = price.toString()
            if(sessionData.isNotEmpty())
                postSessionData(SessionInfoRequest(location_id, minutesSpent?.toInt(), end_time))
        }
    }

    private fun setSessionDataUI(sessionData: SessionData) {
        sessionData.run {
            locationId.value = location_id
            locationDetail.value = location_details
            pricePerMin.value = price_per_min.toString()
            sesseionStartTime.value =  getSessionTimeFormat().format(start_time)
            endTime.value =  getSessionTimeFormat().format(end_time)
        }
    }

    private fun calculateSessionPrice(st: Long?, en: Long?): Long? {
        val diffMs = st?.let { en?.minus(it) }
        val diffSec = diffMs?.div(1000)
        return diffSec?.let { if(it > 60 )it.div(60) else 1 }
    }

    private fun postSessionData(sessionInfoRequest: SessionInfoRequest) {
        bindDisposable {
            homeRepository.postSessionData(sessionInfoRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    it.printStackTrace()
                })
        }
    }

    fun resetSession() {
        bindDisposable {
            homeRepository.deleteSessionData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    it.printStackTrace()
                })
        }
        setScanNowState()
    }

    companion object {
        fun provideFactory(
            assistedFactory: HomeViewModelFactory,
            plantId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(plantId) as T
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}

@AssistedFactory
interface  HomeViewModelFactory {
    fun create(sessionData: String): HomeViewModel
}