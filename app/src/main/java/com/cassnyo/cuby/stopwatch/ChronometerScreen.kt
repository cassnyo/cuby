package com.cassnyo.cuby.stopwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.cassnyo.cuby.ui.theme.CubyTheme

@Composable
fun ChronometerScreen(
    viewModel: ChronometerViewModel,
) {
    val state by viewModel.state.collectAsState()

    ChronometerScreenContent(
        elapsedTime = state.elapsedTimestamp,
        onClick = { viewModel.onTimerClick() }
    )
}

@Composable
private fun ChronometerScreenContent(
    elapsedTime: Long,
    onClick: () -> Unit,
) {


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
            )
            .clickable(
                onClick = onClick,
            ),
    ) {
        Text(
            text = formatMilliseconds(elapsedTime),
            fontSize = TextUnit(
                value = 18f,
                type = TextUnitType.Sp,
            ),
        )
    }
}

fun formatMilliseconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val remainingMilliseconds = milliseconds % 1000

    return String.format("%02d:%02d.%02d", minutes, seconds, remainingMilliseconds)
}

@Preview
@Composable
private fun ChronometerScreenPreview() {
    CubyTheme {
        ChronometerScreenContent(
            elapsedTime = 1000,
            onClick = {}
        )
    }
}