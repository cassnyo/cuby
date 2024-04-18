package com.cassnyo.cuby.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cassnyo.cuby.data.SolvesRepository
import com.cassnyo.cuby.data.database.entity.PenaltyTypeEntity
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
import java.time.LocalDateTime

class ChronometerViewModel(
    private val chronometer: Chronometer,
    private val scrambleGenerator: ScrambleGenerator,
    private val solvesRepository: SolvesRepository,
) : ViewModel() {

    private val scrambleFlow = MutableStateFlow<State.ScrambleState>(State.ScrambleState.Loading)
    private val timerFlow = MutableStateFlow(
        value = State.Timer(
            isRunning = false,
            elapsedTimestamp = 0L
        )
    )
    private val lastSolveFlow = MutableStateFlow<State.LastSolve?>(null)
    private val statisticsFlow = MutableStateFlow(
        value = State.Statistics(
            count = 0,
            averageOf5 = 0L,
            averageOf12 = 0L,
        )
    )

    data class State(
        val scramble: ScrambleState,
        val timer: Timer,
        val statistics: Statistics,
        val lastSolve: LastSolve?,
    ) {
        sealed class ScrambleState {
            data object Loading : ScrambleState()
            data class Generated(val scramble: Scramble) : ScrambleState()
        }

        data class Timer(
            val isRunning: Boolean,
            val elapsedTimestamp: Long,
        )

        data class LastSolve(
            val id: Long,
            val penalty: PenaltyType?,
            val time: Long,
        ) {
            enum class PenaltyType {
                DNF, PLUS_TWO,
            }
        }

        data class Statistics(
            val count: Int,
            val averageOf5: Long,
            val averageOf12: Long,
        )
    }

    val state: StateFlow<State> =
        combine(
            scrambleFlow,
            timerFlow,
            lastSolveFlow,
            statisticsFlow,
        ) { scramble, timer, solve, statistics ->
            State(
                scramble = scramble,
                timer = timer,
                lastSolve = solve,
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
        val timer = timerFlow.value
        if (timer.isRunning) {
            chronometer.stop()
            viewModelScope.launch {
                val solveId = solvesRepository.saveSolve(
                    solve = SolveEntity(
                        scramble = scrambleFlow.value.let {
                            it as? State.ScrambleState.Generated
                        }?.scramble?.moves.orEmpty(),
                        time = timer.elapsedTimestamp,
                        penalty = null,
                        createdAt = LocalDateTime.now(),
                    )
                ).id
                lastSolveFlow.update {
                    State.LastSolve(
                        id = solveId,
                        time = timer.elapsedTimestamp,
                        penalty = null,
                    )
                }
            }
            timerFlow.update { it.copy(isRunning = false) }
            generateScramble()
        } else {
            timerFlow.update { it.copy(isRunning = true) }
            viewModelScope.launch {
                chronometer
                    .start()
                    .collect { elapsedTime ->
                        timerFlow.update { it.copy(elapsedTimestamp = elapsedTime) }
                    }
            }
        }
    }

    fun onDeleteSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.deleteSolve(solveId)
            timerFlow.update { it.copy(elapsedTimestamp = 0L) }
            lastSolveFlow.update { null }
        }
    }

    fun onDNFSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.setPenaltyToSolve(solveId, PenaltyTypeEntity.DNF)
            lastSolveFlow.update {
                it?.copy(penalty = State.LastSolve.PenaltyType.DNF)
            }
        }
    }

    fun onPlusTwoSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.setPenaltyToSolve(solveId, PenaltyTypeEntity.PLUS_TWO)
            lastSolveFlow.update {
                it?.copy(penalty = State.LastSolve.PenaltyType.PLUS_TWO)
            }
        }
    }

    private fun generateScramble() {
        scrambleFlow.update { State.ScrambleState.Loading }
        viewModelScope.launch {
            val newScramble = scrambleGenerator.generate()
            scrambleFlow.update {
                State.ScrambleState.Generated(scramble = newScramble)
            }
        }
    }

    private fun observeStatistics() {
        viewModelScope.launch {
            combine(
                solvesRepository.observeTimes(),
                solvesRepository.observeAverageOf(count = 5),
                solvesRepository.observeAverageOf(count = 12),
            ) { times, averageOf5, averageOf12 ->
                state.value.statistics.copy(
                    count = times.size,
                    averageOf5 = averageOf5,
                    averageOf12 = averageOf12,
                )
            }.collect { newStatistics ->
                statisticsFlow.update { newStatistics }
            }
        }
    }

    private companion object {
        fun initialState() = State(
            scramble = State.ScrambleState.Loading,
            timer = State.Timer(
                isRunning = false,
                elapsedTimestamp = 0L,
            ),
            lastSolve = null,
            statistics = State.Statistics(
                count = 0,
                averageOf5 = 0L,
                averageOf12 = 0L,
            ),
        )
    }
}