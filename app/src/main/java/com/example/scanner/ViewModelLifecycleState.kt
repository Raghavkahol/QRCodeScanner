package com.example.scanner

import android.content.Intent

sealed class ViewModelLifecycleState{
    data class actionOnSessionState(val startTimerService: Boolean) : ViewModelLifecycleState()
}

