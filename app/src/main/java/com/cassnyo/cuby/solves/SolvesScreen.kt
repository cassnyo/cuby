package com.cassnyo.cuby.solves

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cassnyo.cuby.R
import com.cassnyo.cuby.common.ui.TimeFormatter
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType.DNF
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType.PLUS_TWO
import com.cassnyo.cuby.data.repository.solves.model.Solve
import com.cassnyo.cuby.solves.SolvesViewModel.State
import com.cassnyo.cuby.solves.SolvesViewModel.State.SolvesState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SolvesScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<SolvesViewModel>()
    val state by viewModel.state.collectAsState()

    SolvesScreenContent(
        state = state,
        modifier = modifier,
    )
}

@Composable
fun SolvesScreenContent(
    state: State,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (state.solvesState) {
            is SolvesState.Loading -> {
                SolvesLoading(modifier = Modifier.align(Alignment.Center))
            }

            is SolvesState.Content -> {
                SolvesContent(solves = state.solvesState.solves)
            }
        }
    }
}

@Composable
private fun SolvesLoading(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier,
    )
}

@Composable
private fun SolvesContent(
    solves: List<Solve>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        modifier = modifier,
    ) {
        items(
            count = solves.size,
            key = { index -> solves[index].id },
        ) { index ->
            SolveItem(
                solve = solves[index]
            )
        }
    }
}

@Composable
private fun SolveItem(
    solve: Solve,
    modifier: Modifier = Modifier
) {
    val solveTimeText = when (solve.penalty) {
        DNF -> stringResource(id = R.string.common_penalty_dnf)
        PLUS_TWO,
        null -> TimeFormatter.formatMilliseconds(solve.time)
    }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(all = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = solve.createdAt.format(DateTimeFormatter.ofPattern("dd/MM")),
                    style = MaterialTheme.typography.bodySmall
                )

                if (solve.penalty == PLUS_TWO) {
                    Text(
                        text = stringResource(id = R.string.common_penalty_plus_two),
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(height = 2.dp))
            Text(
                text = solveTimeText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
        }
    }

}

@Preview
@Composable
private fun PreviewSolvesScreen() {
    SolvesScreenContent(
        state = State(
            solvesState = SolvesState.Content(
                solves = listOf(
                    stubSolve(id = 1L),
                    stubSolve(id = 2L, penalty = DNF),
                    stubSolve(id = 3L, penalty = PLUS_TWO),
                    stubSolve(id = 4L),
                    stubSolve(id = 5L),
                    stubSolve(id = 6L),
                    stubSolve(id = 7L),
                    stubSolve(id = 8L),
                    stubSolve(id = 9L),
                    stubSolve(id = 10L),
                )
            )
        )
    )
}

@Preview
@Composable
private fun PreviewSolveItem() {
    SolveItem(solve = stubSolve())
}

@Preview
@Composable
private fun PreviewSolveItemWithDnfPenalty() {
    SolveItem(solve = stubSolve(penalty = DNF))
}

@Preview
@Composable
private fun PreviewSolveItemWithPlusTwoPenalty() {
    SolveItem(solve = stubSolve(penalty = PLUS_TWO))
}

private fun stubSolve(
    id: Long = 1L,
    penalty: PenaltyType? = null,
) = Solve(
    id = id,
    scramble = "B2 R U L B' F' U F B U2 F D R' F' U B2 R2 B2 L R2 U D' R' L D'",
    time = 1000L,
    penalty = penalty,
    createdAt = LocalDateTime.now(),
)