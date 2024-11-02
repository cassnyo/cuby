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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChronometerViewModel @Inject constructor(
    private val chronometer: Chronometer,
    private val scrambleGenerator: ScrambleGenerator,
    private val solvesRepository: SolvesRepository,
    private val statisticsRepository: StatisticsRepository,
) : ViewModel() {

    private val scrambleFlow = MutableStateFlow<State.ScrambleState>(State.ScrambleState.Loading)
    private val lastSolveIdFlow = MutableStateFlow<Long?>(null)

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
            val elapsedTime: Long,
        )
    }

    val state: StateFlow<State> =
        combine(
            scrambleFlow,
            chronometer.elapsedTimeFlow(),
            lastSolveFlow(),
            statisticsFlow(),
        ) { scramble, elapsedTime, lastSolve, statistics ->
            val timer = State.Timer(
                isRunning = chronometer.isRunning(),
                elapsedTime = elapsedTime,
            )

            State(
                scramble = scramble,
                timer = timer,
                lastSolve = lastSolve,
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
                time = state.value.timer.elapsedTime,
            )
        } else {
            chronometer.start()
            // Clear lastSolveId so when the chronometer gets stopped we won't show the previous
            // solve to the user while the new solve is being fetched
            lastSolveIdFlow.tryEmit(null)
        }
    }

    fun onDeleteSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.deleteSolve(solveId)
            lastSolveIdFlow.tryEmit(null)
            chronometer.reset()
        }
    }

    fun onDNFSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.setPenaltyToSolve(solveId, PenaltyType.DNF)
        }
    }

    fun onPlusTwoSolveClicked(solveId: Long) {
        viewModelScope.launch {
            solvesRepository.setPenaltyToSolve(solveId, PenaltyType.PLUS_TWO)
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

    private fun statisticsFlow(): Flow<Statistics?> = statisticsRepository.observeStatistics()

    private fun lastSolveFlow(): Flow<Solve?> =
        lastSolveIdFlow
            .flatMapLatest { lastSolveId ->
                if (lastSolveId == null) {
                    flowOf(null)
                } else {
                    solvesRepository.observeSolve(lastSolveId)
                }
            }

    private fun saveSolve(
        scramble: Scramble,
        time: Long,
    ) = viewModelScope.launch {
        val newSolve = solvesRepository.saveSolve(
            scramble = scramble.moves,
            time = time,
        )
        lastSolveIdFlow.tryEmit(newSolve.id)
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
            elapsedTime = 0L
        )
    }
}