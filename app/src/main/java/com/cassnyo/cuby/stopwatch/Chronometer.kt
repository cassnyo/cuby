package com.cassnyo.cuby.stopwatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration

class Chronometer {

    private var timerJob: Job? = null
    private var startTime: Long = 0L
    private val elapsedTime = MutableSharedFlow<Long>()


    fun start(): Flow<Long> {
        if (timerJob == null) {
            startTime = System.currentTimeMillis()
            timerJob = CoroutineScope(Dispatchers.Default).launch {
                while(true) {
                    elapsedTime.emit(System.currentTimeMillis() - startTime)
                    delay(PERIOD_MILLISECONDS)
                }
            }
        }
        return elapsedTime
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

     private companion object {
        const val PERIOD_MILLISECONDS = 10L
    }

}