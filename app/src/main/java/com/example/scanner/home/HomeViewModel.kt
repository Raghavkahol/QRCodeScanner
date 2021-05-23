package com.example.scanner.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scanner.data.SessionData
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(private val homeRepository: HomeRepository,
                                                 @Assisted private val sessionData: String) : ViewModel() {

     var isSessionStarted = MutableLiveData<Boolean>().apply { false }

    init {
        setSessionState()
    }

    private fun setSessionState() {
        if(sessionData.isEmpty()) {
            isSessionStarted.value = false
        } else {
            val session = parseSessionData(sessionData)
            handleSessionState(session)
        }
    }

    private fun parseSessionData(sessionData: String) : SessionData {
        return Gson().fromJson(sessionData, SessionData::class.java)
    }

    private fun handleSessionState(sessionData: SessionData) {
        if(sessionData.is_session_progress == true) {
            isSessionStarted.value = true
        } else {
            sessionData.is_session_progress = false
            isSessionStarted.value = true
        }
        homeRepository.postSessionData(sessionData)
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