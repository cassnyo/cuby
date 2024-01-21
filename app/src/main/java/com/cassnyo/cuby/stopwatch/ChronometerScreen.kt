package com.cassnyo.cuby.stopwatch

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.PictureDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.cassnyo.cuby.stopwatch.ChronometerViewModel.State.ScrambleState
import com.cassnyo.cuby.stopwatch.scramblegenerator.Scramble
import com.cassnyo.cuby.ui.theme.CubyTheme
import com.caverock.androidsvg.SVG


@Composable
fun ChronometerScreen(
    viewModel: ChronometerViewModel,
) {
    val state by viewModel.state.collectAsState()

    ChronometerScreenContent(
        state = state,
        onTimerClick = { viewModel.onTimerClick() },
        onScrambleClick = {viewModel.onScrambleClick() },
    )
}

@Composable
private fun ChronometerScreenContent(
    state: ChronometerViewModel.State,
    onTimerClick: () -> Unit,
    onScrambleClick: () -> Unit,
) {


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
            )
    ) {
        Button(
           onClick = onTimerClick,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(text = "Timer")
        }
        Button(
            onClick = onScrambleClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(text = "Scramble")
        }
        Text(
            text = formatMilliseconds(state.elapsedTimestamp),
            fontSize = TextUnit(
                value = 18f,
                type = TextUnitType.Sp,
            ),
        )

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            when(state.scramble) {
                is ScrambleState.Generated -> Scramble(state.scramble.scramble)
                is ScrambleState.Loading -> LoadingScramble()
            }
        }
    }
}

fun formatMilliseconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val remainingMilliseconds = milliseconds % 1000

    return String.format("%02d:%02d.%02d", minutes, seconds, remainingMilliseconds)
}

@Composable
private fun Scramble(
    scramble: Scramble,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = scramble.moves,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.displaySmall,
        )
        val svg = SVG.getFromString(scramble.image)
        val picture = svg.renderToPicture()
        val pictureDrawable = PictureDrawable(picture)
        val bitmap = Bitmap.createBitmap(pictureDrawable.intrinsicWidth, pictureDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawPicture(pictureDrawable.getPicture())
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
        )
    }
}

@Composable
private fun LoadingScramble(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier,
    )
}

@Preview
@Composable
private fun ChronometerScreenPreview() {
    CubyTheme {
        ChronometerScreenContent(
            state = ChronometerViewModel.State(
                scramble = ScrambleState.Loading,
                timerStarted = true,
                elapsedTimestamp = 1000,
            ),
            onTimerClick = {},
            onScrambleClick = {},
        )
    }
}