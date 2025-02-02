package com.cassnyo.cuby.solves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cassnyo.cuby.data.repository.solves.SolvesRepository
import com.cassnyo.cuby.data.repository.solves.model.Solve
import com.cassnyo.cuby.solves.SolvesViewModel.State.SolvesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SolvesViewModel @Inject constructor(
    solvesRepository: SolvesRepository,
) : ViewModel() {

    data class State(
        val solvesState: SolvesState,
    ) {
        sealed class SolvesState {
            data object Loading : SolvesState()
            data class Content(val solves: List<Solve>) : SolvesState()
        }
    }

    val state: StateFlow<State> =
        // TODO Add filtering
        solvesRepository.observeAllSolves()
            .map { solves ->
                State(
                    solvesState = SolvesState.Content(solves),
                )
            }.stateIn(viewModelScope, WhileSubscribed(5000), initialState())

    private companion object {
        fun initialState() = State(
            solvesState = SolvesState.Loading,
        )
    }

}