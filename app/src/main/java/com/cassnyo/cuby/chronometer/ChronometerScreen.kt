package com.cassnyo.cuby.chronometer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cassnyo.cuby.R
import com.cassnyo.cuby.chronometer.ChronometerViewModel.State
import com.cassnyo.cuby.chronometer.ChronometerViewModel.State.ScrambleState
import com.cassnyo.cuby.chronometer.scramblegenerator.Scramble
import com.cassnyo.cuby.common.ui.TimeFormatter
import com.cassnyo.cuby.common.ui.composable.ScrambleImage
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType
import com.cassnyo.cuby.data.repository.solves.model.Solve
import com.cassnyo.cuby.data.repository.statistics.model.Statistics
import com.cassnyo.cuby.ui.theme.CubyTheme
import com.cassnyo.cuby.ui.theme.highlightTextOnBackgroundDark
import com.cassnyo.cuby.ui.theme.iconOnBackgroundDark
import com.cassnyo.cuby.ui.theme.textOnBackground
import java.time.LocalDateTime

@Composable
fun ChronometerScreen() {
    val viewModel = hiltViewModel<ChronometerViewModel>()
    val state by viewModel.state.collectAsState()

    ChronometerScreenContent(
        state = state,
        onGenerateScrambleClick = { viewModel.onGenerateScrambleClick() },
        onEditScrambleClick = {},
        onTimerClick = { viewModel.onTimerClick() },
        onDeleteSolveClicked = { solveId -> viewModel.onDeleteSolveClicked(solveId) },
        onDNFSolveClicked = { solveId -> viewModel.onDNFSolveClicked(solveId) },
        onPlusTwoSolveClicked = { solveId -> viewModel.onPlusTwoSolveClicked(solveId) },
    )
}

@Composable
private fun ChronometerScreenContent(
    state: State,
    onGenerateScrambleClick: () -> Unit,
    onEditScrambleClick: () -> Unit,
    onDeleteSolveClicked: (Long) -> Unit,
    onDNFSolveClicked: (Long) -> Unit,
    onPlusTwoSolveClicked: (Long) -> Unit,
    onTimerClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onTimerClick)
    ) {
        AnimatedVisibility(
            visible = !state.timer.isRunning,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            Scramble(
                scrambleState = state.scramble,
                onGenerateScrambleClick = onGenerateScrambleClick,
                onEditScrambleClick = onEditScrambleClick,
            )
        }

        Timer(
            timer = state.timer,
            lastSolve = state.lastSolve,
            onDeleteSolveClicked = onDeleteSolveClicked,
            onDNFSolveClicked = onDNFSolveClicked,
            onPlusTwoSolveClicked = onPlusTwoSolveClicked,
            modifier = Modifier
                .align(Alignment.Center)
        )

        AnimatedVisibility(
            visible = !state.timer.isRunning,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Footer(
                scramble = state.scramble,
                statistics = state.statistics,
            )
        }
    }
}

@Composable
private fun Scramble(
    scrambleState: ScrambleState,
    onGenerateScrambleClick: () -> Unit,
    onEditScrambleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(
                horizontal = 32.dp,
                vertical = 16.dp,
            )
            .fillMaxWidth(),
    ) {
        when (scrambleState) {
            is ScrambleState.Generated -> {
                ScrambleSequence(
                    scramble = scrambleState.scramble,
                    onGenerateClick = onGenerateScrambleClick,
                    onEditClick = onEditScrambleClick
                )
            }

            is ScrambleState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun ScrambleSequence(
    scramble: Scramble,
    onGenerateClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = scramble.moves,
            color = highlightTextOnBackgroundDark,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Row {
            IconButton(onClick = onGenerateClick) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Refresh",
                    tint = iconOnBackgroundDark,
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = iconOnBackgroundDark,
                )
            }
        }
    }
}

@Composable
private fun Timer(
    timer: State.Timer,
    lastSolve: Solve?,
    onDeleteSolveClicked: (Long) -> Unit,
    onDNFSolveClicked: (Long) -> Unit,
    onPlusTwoSolveClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dividerSize by animateDpAsState(
        targetValue = if (timer.isRunning) 0.dp else 240.dp,
        label = "Timer divider size",
    )
    val timerText = if (lastSolve == null || timer.isRunning) {
        TimeFormatter.formatMilliseconds(timer.elapsedTime)
    } else {
        when (lastSolve.penalty) {
            PenaltyType.DNF -> stringResource(id = R.string.common_penalty_dnf)
            PenaltyType.PLUS_TWO ->
                "${TimeFormatter.formatMilliseconds(lastSolve.time)} ${stringResource(id = R.string.common_penalty_plus_two)}"
            else -> TimeFormatter.formatMilliseconds(lastSolve.time)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = timerText,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = highlightTextOnBackgroundDark,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(
            modifier = Modifier.height(6.dp)
        )
        HorizontalDivider(
            color = iconOnBackgroundDark,
            thickness = 2.dp,
            modifier = Modifier.width(dividerSize)
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (lastSolve != null) {
            AnimatedVisibility(
                visible = !timer.isRunning,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Row {
                    IconButton(
                        onClick = { onDeleteSolveClicked(lastSolve.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.chronometer_timer_button_delete),
                            tint = iconOnBackgroundDark,
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    TextButton(
                        onClick = { onDNFSolveClicked(lastSolve.id) }
                    ) {
                        Text(
                            text = stringResource(R.string.chronometer_timer_button_dnf),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = iconOnBackgroundDark,
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    TextButton(
                        onClick = { onPlusTwoSolveClicked(lastSolve.id) }
                    ) {
                        Text(
                            text = stringResource(R.string.chronometer_timer_button_plus_two),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = iconOnBackgroundDark,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeftStatistics(
    count: Int,
    median: Long,
    bestSolve: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        StatisticsRow(label = stringResource(R.string.chronometer_statistics_count), value = count.toString())
        StatisticsTimedRow(label = stringResource(R.string.chronometer_statistics_median), value = median)
        StatisticsTimedRow(label = stringResource(R.string.chronometer_statistics_best), value = bestSolve)
    }
}

@Composable
private fun RightStatistics(
    averageOf5: Long,
    averageOf12: Long,
    averageOf50: Long,
    averageOf100: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier,
    ) {
        StatisticsTimedRow(label = stringResource(R.string.chronometer_statistics_ao5), value = averageOf5)
        StatisticsTimedRow(label = stringResource(R.string.chronometer_statistics_ao12), value = averageOf12)
        StatisticsTimedRow(label = stringResource(R.string.chronometer_statistics_ao50), value = averageOf50)
        StatisticsTimedRow(label = stringResource(R.string.chronometer_statistics_ao100), value = averageOf100)
    }
}

@Composable
private fun Footer(
    scramble: ScrambleState,
    statistics: Statistics?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        statistics?.let {
            LeftStatistics(
                count = statistics.count,
                median = statistics.median,
                bestSolve = statistics.bestSolve,
            )
        }

        when (scramble) {
            is ScrambleState.Loading ->
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 20.dp),
                )
            is ScrambleState.Generated ->
                ScrambleImage(
                    imageSvg = scramble.scramble.image,
                )
        }

        statistics?.let {
            RightStatistics(
                averageOf5 = statistics.averageOf5,
                averageOf12 = statistics.averageOf12,
                averageOf50 = statistics.averageOf50,
                averageOf100 = statistics.averageOf100,
            )
        }
    }
}

@Composable
private fun StatisticsRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = "${label}: ",
            color = textOnBackground,
        )
        Text(
            text = value,
            color = highlightTextOnBackgroundDark,
        )
    }
}

@Composable
private fun StatisticsTimedRow(
    label: String,
    value: Long,
    modifier: Modifier = Modifier,
) {
    StatisticsRow(
        label = label,
        value = TimeFormatter.formatMilliseconds(value),
        modifier = modifier,
    )
}

@Preview
@Composable
private fun ChronometerScreenPreview() {
    CubyTheme {
        ChronometerScreenContent(
            state = State(
                scramble = ScrambleState.Loading,
                timer = State.Timer(
                    isRunning = false,
                    elapsedTime = 1000L,
                ),
                lastSolve = Solve(
                    id = 0,
                    scramble = "",
                    time = 1000L,
                    penalty = null,
                    createdAt = LocalDateTime.now(),
                ),
                statistics = Statistics(
                    count = 50,
                    bestSolve = 20,
                    median = 2000,
                    averageOf5 = 2000,
                    averageOf12 = 2000,
                    averageOf50 = 2000,
                    averageOf100 = 2000,
                )
            ),
            onGenerateScrambleClick = {},
            onEditScrambleClick = {},
            onTimerClick = {},
            onDeleteSolveClicked = {},
            onDNFSolveClicked = {},
            onPlusTwoSolveClicked = {},
        )
    }
}