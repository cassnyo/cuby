package com.cassnyo.cuby.chronometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cassnyo.cuby.chronometer.scramblegenerator.Scramble
import com.cassnyo.cuby.chronometer.scramblegenerator.ScrambleGenerator
import com.cassnyo.cuby.data.repository.solves.SolvesRepository
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType
import com.cassnyo.cuby.data.repository.solves.model.Solve
import com.cassnyo.cuby.data.repository.statistics.StatisticsRepository
import com.cassnyo.cuby.data.repository.statistics.model.Statistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChronometerViewModel @Inject constructor(
    private val chronometer: Chronometer,
    private val scrambleGenerator: ScrambleGenerator,
    private val solvesRepository: SolvesRepository,
    private val statisticsRepository: StatisticsRepository,
) : ViewModel() {

    private val scrambleFlow = MutableStateFlow<State.ScrambleState>(State.ScrambleState.Loading)
    private val lastSolveFlow = MutableStateFlow<Solve?>(null)

    data class State(
        val scramble: ScrambleState,
        val timer: Timer,
        val statistics: Statistics?,
        val lastSolve: Solve?,
    ) {
        sealed class ScrambleState {
            data object Loading : ScrambleState()
            data class Generated(val scramble: Scramble) : ScrambleState()
        }

        data class Timer(
            val isRunning: Boolean,
            val elapsedTimestamp: Long,
        )
    }

    val state: StateFlow<State> =
        combine(
            scrambleFlow,
            chronometer.elapsedTimeFlow(),
            lastSolveFlow,
            statisticsFlow(),
        ) { scramble, elapsedTime, solve, statistics ->
            val timer = State.Timer(
                isRunning = chronometer.isRunning(),
                elapsedTimestamp = elapsedTime,
            )

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
    }

    fun onGenerateScrambleClick() {
        generateScramble()
    }

    fun onTimerClick() {
        if (chronometer.isRunning()) {
            chronometer.stop()
            saveSolve(
                scramble = (state.value.scramble as State.ScrambleState.Generated).scramble,
                time = state.value.timer.elapsedTimestamp,
            )
        } else {
            chronometer.start()
        }
    }

    fun onDeleteSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.deleteSolve(solveId)
            lastSolveFlow.update { null }
            chronometer.reset()
        }
    }

    fun onDNFSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.setPenaltyToSolve(solveId, PenaltyType.DNF)
            lastSolveFlow.update {
                it?.copy(penalty = PenaltyType.DNF)
            }
        }
    }

    fun onPlusTwoSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.setPenaltyToSolve(solveId, PenaltyType.PLUS_TWO)
            lastSolveFlow.update {
                it?.copy(penalty = PenaltyType.PLUS_TWO)
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

    private fun statisticsFlow() = statisticsRepository.observeStatistics()

    private fun saveSolve(
        scramble: Scramble,
        time: Long,
    ) = viewModelScope.launch {
        val solve = solvesRepository.saveSolve(
            scramble = scramble.moves,
            time = time,
        )
        lastSolveFlow.tryEmit(solve)
    }

    private companion object {
        fun initialState() = State(
            scramble = State.ScrambleState.Loading,
            timer = initialTimer(),
            lastSolve = null,
            statistics = null,
        )

        fun initialTimer() = State.Timer(
            isRunning = false,
            elapsedTimestamp = 0L
        )
    }
}