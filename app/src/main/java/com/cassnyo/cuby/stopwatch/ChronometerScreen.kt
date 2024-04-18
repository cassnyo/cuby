package com.cassnyo.cuby.stopwatch

import android.graphics.Canvas
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.cassnyo.cuby.stopwatch.ChronometerViewModel.State
import com.cassnyo.cuby.stopwatch.ChronometerViewModel.State.ScrambleState
import com.cassnyo.cuby.stopwatch.scramblegenerator.Scramble
import com.cassnyo.cuby.ui.theme.CubyTheme
import com.cassnyo.cuby.ui.theme.highlightTextOnBackgroundDark
import com.cassnyo.cuby.ui.theme.iconOnBackgroundDark
import com.cassnyo.cuby.ui.theme.textOnBackground
import com.caverock.androidsvg.SVG
import kotlin.math.roundToInt

@Composable
fun ChronometerScreen(
    viewModel: ChronometerViewModel,
) {
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
            Statistics(
                statistics = state.statistics,
            )
        }
    }
}

fun formatMilliseconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val remainingMilliseconds = milliseconds % 1000

    val formattedMinutes = if (minutes > 0) "$minutes:" else ""
    val formattedSeconds = if (minutes > 0) String.format("%02d", seconds) else seconds
    val formattedMilliseconds = String.format("%02d", remainingMilliseconds / 10)

    return "$formattedMinutes$formattedSeconds.$formattedMilliseconds"
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
        // TODO Generate the image with a bigger size
        // ScrambleImage(
        //     imageSvg = scramble.image,
        // )
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
private fun ScrambleImage(
    imageSvg: String,
    modifier: Modifier = Modifier,
) {
    val image = remember {
        val svg = SVG.getFromString(imageSvg)
        val bitmap = createBitmap(
            width = svg.documentWidth.roundToInt(),
            height = svg.documentHeight.roundToInt()
        )
        val canvas = Canvas(bitmap)
        svg.renderToCanvas(canvas)
        bitmap.asImageBitmap()
    }

    Image(
        bitmap = image,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
private fun Timer(
    timer: State.Timer,
    lastSolve: State.LastSolve?,
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
        formatMilliseconds(timer.elapsedTimestamp)
    } else {
        when (lastSolve.penalty) {
            State.LastSolve.PenaltyType.DNF -> "DNF"
            State.LastSolve.PenaltyType.PLUS_TWO -> "${formatMilliseconds(lastSolve.time)} +2"
            else -> formatMilliseconds(lastSolve.time)
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
                            contentDescription = "Delete solve",
                            tint = iconOnBackgroundDark,
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    TextButton(
                        onClick = { onDNFSolveClicked(lastSolve.id) }
                    ) {
                        Text(
                            text = "DNF",
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
                            text = "+2",
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
private fun Statistics(
    statistics: State.Statistics,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Count: ${statistics.count}",
            color = textOnBackground,
        )
        Text(
            text = "Ao5: ${formatMilliseconds(statistics.averageOf5)}",
            color = textOnBackground,
        )
        Text(
            text = "Ao12: ${formatMilliseconds(statistics.averageOf12)}",
            color = textOnBackground,
        )
    }
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
                    elapsedTimestamp = 1000L,
                ),
                lastSolve = State.LastSolve(
                    id = 0,
                    time = 1000L,
                    penalty = null,
                ),
                statistics = State.Statistics(
                    count = 50,
                    averageOf5 = 2000,
                    averageOf12 = 2000,
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