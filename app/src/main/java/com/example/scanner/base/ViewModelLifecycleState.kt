package com.example.scanner.base

sealed class ViewModelLifecycleState{
    data class actionOnSessionState(val startTimerService: Boolean, val startTime: Long) : ViewModelLifecycleState()
    data class showToast(val message: String) : ViewModelLifecycleState()
}

