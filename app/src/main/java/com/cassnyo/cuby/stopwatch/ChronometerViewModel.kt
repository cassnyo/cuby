package com.cassnyo.cuby.stopwatch

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ChronometerViewModel(
    private val chronometer: Chronometer,
) : ViewModel() {

    data class State(
        val timerStarted: Boolean,
        val elapsedTimestamp: Long
    )

    val state = MutableStateFlow(
        value = State(
            timerStarted = false,
            elapsedTimestamp = 0,
        )
    )

    fun onTimerClick() {
        if (state.value.timerStarted) {
            chronometer.stop()
            state.value = state.value.copy(
                timerStarted = false
            )
        } else {
            state.value = state.value.copy(
                timerStarted = true
            )
            viewModelScope.launch {
                chronometer
                    .start()
                    .collect { elapsedTime ->
                        state.value = state.value.copy(
                            elapsedTimestamp = elapsedTime,
                        )
                    }
            }
        }
    }





}