package com.cassnyo.cuby.solves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cassnyo.cuby.chronometer.scramblegenerator.ScrambleImageGenerator
import com.cassnyo.cuby.data.repository.solves.SolvesRepository
import com.cassnyo.cuby.data.repository.solves.model.Solve
import com.cassnyo.cuby.solves.SolvesViewModel.State.SolvesState
import com.cassnyo.cuby.solves.SolvesViewModel.State.SolvesState.Content.SolveDetailsDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worldcubeassociation.tnoodle.svglite.Svg
import javax.inject.Inject

@HiltViewModel
class SolvesViewModel @Inject constructor(
    private val solvesRepository: SolvesRepository,
    private val scrambleImageGenerator: ScrambleImageGenerator,
) : ViewModel() {

    data class State(
        val solvesState: SolvesState,
    ) {
        sealed class SolvesState {
            data object Loading : SolvesState()
            data class Content(
                val solves: List<Solve>,
                val solveDetailsDialog: SolveDetailsDialog? = null,
            ) : SolvesState() {

                data class SolveDetailsDialog(
                    val solve: Solve,
                    val scrambleImage: Svg,
                )

            }
        }
    }

    private val solvesFlow = solvesRepository.observeAllSolves()
    private val selectedSolveFlow = MutableStateFlow<Solve?>(null)
    private val solveDetailsDialogFlow: StateFlow<SolveDetailsDialog?> =
        selectedSolveFlow
            .map { solve ->
                if (solve == null) return@map null

                val scrambleImage = scrambleImageGenerator.generateImage(solve.scramble)
                SolveDetailsDialog(
                    solve = solve,
                    scrambleImage = scrambleImage,
                )
            }.stateIn(viewModelScope, WhileSubscribed(5000), null)

    val state: StateFlow<State> =
        combine(
            solvesFlow,
            solveDetailsDialogFlow,
        ) { solvesState, solveDetailsDialog ->
            State(
                solvesState = SolvesState.Content(
                    solves = solvesState,
                    solveDetailsDialog = solveDetailsDialog,
                )
            )
        }.stateIn(viewModelScope, WhileSubscribed(5000), initialState())

    fun onSolveClick(solve: Solve) {
        selectedSolveFlow.update { solve }
    }

    fun onSolveDetailsDialogDismissRequest() {
        selectedSolveFlow.update { null }
    }

    fun onSolveDetailsDialogDeleteClick(solve: Solve) {
        selectedSolveFlow.update { null }
        viewModelScope.launch(Dispatchers.IO) {
            solvesRepository.deleteSolve(solveId = solve.id)
        }
    }

    private companion object {
        fun initialState() = State(
            solvesState = SolvesState.Loading,
        )
    }

}