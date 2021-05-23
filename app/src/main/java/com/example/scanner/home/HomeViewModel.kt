package com.example.scanner.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scanner.BaseViewModel
import com.example.scanner.data.SessionData
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

    init {
        getSessionData()
    }

    private fun getSessionData() {
        bindDisposable {
            homeRepository.getSessionData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if(it.isNullOrEmpty()) {
                        if(sessionData.isNotEmpty()) {
                            val session = parseSessionData(sessionData)
                            insertSessionStartData(session)
                            setSessionInProgressState()
                        } else {
                            setScanNowState()
                        }
                    } else {
                        if((sessionData.isNotEmpty() && it[0].start_time != null) || (it[0].end_time != null)){
                            setSessionCompletedState()
                            insertSessionEndData(it[0])
                        } else {
                            setSessionInProgressState()
                            insertSessionStartData(it[0])
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun parseSessionData(sessionData: String) : SessionData {
        var ss = sessionData.substring(1, sessionData.length - 1)
        ss = ss.replace("\\", "")
        return Gson().fromJson(ss, SessionData::class.java)

    }

    private fun insertSessionStartData(sessionData: SessionData) {
        sessionData.start_time = System.currentTimeMillis()
        bindDisposable {
            homeRepository.insertSessionData(sessionData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleSessionStartState(sessionData)
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun handleSessionStartState(sessionData: SessionData) {
        sessionData.run {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            sesseionStartTime.value =  sdf.format(Date())
            locationId.value = location_id
            locationDetail.value = location_details
            pricePerMin.value = price_per_min.toString()
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

    private fun insertSessionEndData(sessionData: SessionData) {
        sessionData.end_time = System.currentTimeMillis()
        bindDisposable {
            homeRepository.insertSessionData(sessionData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleSessionEndState(sessionData)
                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun handleSessionEndState(sessionData: SessionData) {
        sessionData.run {
            val sdf : DateFormat= SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val date = start_time?.let { Date(it) }
            val start = start_time
            sesseionStartTime.value =  sdf.format(date)
            locationId.value = location_id
            locationDetail.value = location_details
            pricePerMin.value = price_per_min.toString()
            endTime.value = sdf.format(end_time?.let { Date(it) })
            val end = end_time
            val price = calculateSessionPrice(start, end, price_per_min)
            sessionPrice.value = price
        }
    }

    private fun calculateSessionPrice(st: Long?, en: Long?, price: Float?): String? {
        val diffMs = st?.let { en?.minus(it) }
        val diffSec = diffMs?.div(1000)
        val min =diffSec?.let { if(it > 60 )it.div(60) else 1 }
        return price?.let { (min?.toFloat()?.times(it)).toString() }
        }

    private fun setSessionCompletedState() {
        isScanStartState.value = false
        isSessionInProgressState.value = false
        isSessionCompletedState.value = true
    }

    private fun postSessionData(sessionData: SessionData) {
        bindDisposable {
            homeRepository.postSessionData(sessionData)
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
}

@AssistedFactory
interface  HomeViewModelFactory {
    fun create(sessionData: String): HomeViewModel
}