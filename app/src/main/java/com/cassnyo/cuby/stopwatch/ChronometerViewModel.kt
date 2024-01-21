package com.cassnyo.cuby.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cassnyo.cuby.stopwatch.scramblegenerator.Scramble
import com.cassnyo.cuby.stopwatch.scramblegenerator.ScrambleGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChronometerViewModel(
    private val chronometer: Chronometer,
    private val scrambleGenerator: ScrambleGenerator,
) : ViewModel() {

    data class State(
        val scramble: ScrambleState,
        val timerStarted: Boolean,
        val elapsedTimestamp: Long,
    ) {
        sealed class ScrambleState {
            data object Loading: ScrambleState()
            data class Generated(val scramble: Scramble): ScrambleState()
        }
    }

    val state = MutableStateFlow(
        value = State(
            scramble = State.ScrambleState.Loading,
            timerStarted = false,
            elapsedTimestamp = 0,
        )
    )

    fun onScrambleClick() {
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