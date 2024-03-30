package com.cassnyo.cuby.stopwatch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cassnyo.cuby.data.TimesRepository
import com.cassnyo.cuby.data.model.Time
import com.cassnyo.cuby.stopwatch.scramblegenerator.Scramble
import com.cassnyo.cuby.stopwatch.scramblegenerator.ScrambleGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class ChronometerViewModel(
    private val chronometer: Chronometer,
    private val scrambleGenerator: ScrambleGenerator,
    private val timesRepository: TimesRepository,
) : ViewModel() {

    data class State(
        val scramble: ScrambleState,
        val timerStarted: Boolean,
        val elapsedTimestamp: Long,
        val statistics: Statistics,
    ) {
        sealed class ScrambleState {
            data object Loading : ScrambleState()
            data class Generated(val scramble: Scramble) : ScrambleState()
        }

        data class Statistics(
            val count: Int,
            val averageOf5: Long,
            val averageOf12: Long,
        )
    }

    val state = MutableStateFlow(
        value = State(
            scramble = State.ScrambleState.Loading,
            timerStarted = false,
            elapsedTimestamp = 0,
            statistics = State.Statistics(
                count = 0,
                averageOf5 = 0L,
                averageOf12 = 0L,
            )
        )
    )

    init {
        generateScramble()
        observeStatistics()
    }

    fun onGenerateScrambleClick() {
        generateScramble()
    }

    fun onTimerClick() {
        if (state.value.timerStarted) {
            chronometer.stop()
            viewModelScope.launch {
                timesRepository.saveTime(
                    time = Time(
                        scramble = state.value.scramble.let {
                            it as? ChronometerViewModel.State.ScrambleState.Generated
                        }?.scramble?.moves.orEmpty(),
                        time = state.value.elapsedTimestamp,
                    )
                )
            }
            state.value = state.value.copy(timerStarted = false)
            generateScramble()
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

    private fun generateScramble() {
        state.value = state.value.copy(
            scramble = State.ScrambleState.Loading
        )
        viewModelScope.launch {
            val scramble = scrambleGenerator.generate()
            state.value = state.value.copy(
                scramble = State.ScrambleState.Generated(
                    scramble = scramble
                )
            )
        }
    }

    private fun observeStatistics() {
        viewModelScope.launch {
            combine(
                timesRepository.observeTimes(),
                timesRepository.observeAverageOf(count = 5),
                timesRepository.observeAverageOf(count = 12),
            ) { times, averageOf5, averageOf12 ->
                state.value.statistics.copy(
                    count = times.size,
                    averageOf5 = averageOf5,
                    averageOf12 = averageOf12,
                )
            }.collect { statistics ->
                state.value = state.value.copy(statistics = statistics)
            }
        }
    }

}