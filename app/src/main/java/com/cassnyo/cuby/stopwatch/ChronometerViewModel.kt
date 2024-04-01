package com.cassnyo.cuby.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cassnyo.cuby.data.TimesRepository
import com.cassnyo.cuby.data.database.entity.SolveEntity
import com.cassnyo.cuby.stopwatch.scramblegenerator.Scramble
import com.cassnyo.cuby.stopwatch.scramblegenerator.ScrambleGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChronometerViewModel(
    private val chronometer: Chronometer,
    private val scrambleGenerator: ScrambleGenerator,
    private val timesRepository: TimesRepository,
) : ViewModel() {

    private val scramble = MutableStateFlow<State.ScrambleState>(State.ScrambleState.Loading)
    private val timerStarted = MutableStateFlow(false)
    private val elapsedTimestamp = MutableStateFlow(0L)
    private val statistics = MutableStateFlow(
        value = State.Statistics(
            count = 0,
            averageOf5 = 0L,
            averageOf12 = 0L,
        )
    )

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

    val state: StateFlow<State> =
        combine(
            scramble,
            timerStarted,
            elapsedTimestamp,
            statistics,
        ) { scramble, timerStarted, ellapsedTimestamp, statistics ->
            State(
                scramble = scramble,
                timerStarted = timerStarted,
                elapsedTimestamp = ellapsedTimestamp,
                statistics = statistics
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialState(),
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
                timesRepository.saveSolve(
                    solve = SolveEntity(
                        scramble = state.value.scramble.let {
                            it as? State.ScrambleState.Generated
                        }?.scramble?.moves.orEmpty(),
                        time = state.value.elapsedTimestamp,
                    )
                )
            }
            timerStarted.update { false }
            generateScramble()
        } else {
            timerStarted.update { true }
            viewModelScope.launch {
                chronometer
                    .start()
                    .collect { elapsedTime ->
                        elapsedTimestamp.update { elapsedTime }
                    }
            }
        }
    }

    private fun generateScramble() {
        scramble.update { State.ScrambleState.Loading }
        viewModelScope.launch {
            val newScramble = scrambleGenerator.generate()
            scramble.update {
                State.ScrambleState.Generated(scramble = newScramble)
            }
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
            }.collect { newStatistics ->
                statistics.update { newStatistics }
            }
        }
    }

    private companion object {
        fun initialState() = State(
            scramble = State.ScrambleState.Loading,
            timerStarted = false,
            elapsedTimestamp = 0,
            statistics = State.Statistics(
                count = 0,
                averageOf5 = 0L,
                averageOf12 = 0L,
            ),
        )
    }
}