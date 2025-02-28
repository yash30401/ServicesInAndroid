package com.yash.servicesinandroid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel:ViewModel() {

    private val _timeLeft  = MutableStateFlow(1500000L)
    val timeLeft:StateFlow<Long> get() = _timeLeft


    private val _extraTime = MutableStateFlow(0)
    val extraTime: StateFlow<Int> get() = _extraTime


    fun updateTimeLeft(time: Long) {
        _timeLeft.value = time
    }

    fun updateExtraTime(time: Int) {
        _extraTime.value = time
    }
}