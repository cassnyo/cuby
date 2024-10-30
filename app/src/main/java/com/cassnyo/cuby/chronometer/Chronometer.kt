package com.cassnyo.cuby.chronometer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class Chronometer @Inject constructor() {

    private var timerJob: Job? = null
    private var startTime: Long = 0L
    private val elapsedTime = MutableStateFlow(0L)

    fun start() {
        if (isRunning()) throw IllegalStateException("Chronometer is already running")

        startTime = System.currentTimeMillis()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while(true) {
                elapsedTime.tryEmit(System.currentTimeMillis() - startTime)
                delay(PERIOD_MILLISECONDS)
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    fun reset() {
        elapsedTime.tryEmit(0L)
    }

    fun isRunning() = timerJob?.isActive ?: false

    fun elapsedTimeFlow(): Flow<Long> = elapsedTime

     private companion object {
        const val PERIOD_MILLISECONDS = 10L
    }

}