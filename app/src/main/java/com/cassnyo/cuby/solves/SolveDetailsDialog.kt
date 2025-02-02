package com.cassnyo.cuby.solves

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cassnyo.cuby.common.ui.TimeFormatter
import com.cassnyo.cuby.common.ui.asString
import com.cassnyo.cuby.common.ui.composable.ScrambleImage
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType
import com.cassnyo.cuby.data.repository.solves.model.Solve
import com.cassnyo.cuby.ui.theme.Typography
import org.worldcubeassociation.tnoodle.svglite.Dimension
import org.worldcubeassociation.tnoodle.svglite.Svg
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun SolveDetailsDialog(
    solve: Solve,
    scrambleImage: Svg,
    onDeleteClick: (Solve) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Header(
                    solveTime = solve.time,
                    penalty = solve.penalty,
                    createdAt = solve.createdAt,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp,
                    )
                )

                HorizontalDivider()

                Scramble(
                    scramble = solve.scramble,
                    scrambleImage = scrambleImage,
                    modifier = Modifier.padding(16.dp)
                )

                HorizontalDivider()

                BottomBar(
                    onDeleteClick = { onDeleteClick(solve) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun Header(
    solveTime: Long,
    penalty: PenaltyType?,
    createdAt: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        TimeWithPenaltyLabel(
            solveTime = solveTime,
            penalty = penalty,
        )
        SolvedAtLabel(
            solvedAt = createdAt,
        )
    }
}

@Composable
private fun Scramble(
    scramble: String,
    scrambleImage: Svg,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = scramble,
        )
        Spacer(modifier = Modifier.height(4.dp))
        ScrambleImage(
            imageSvg = scrambleImage.toString(),
        )
    }
}

@Composable
private fun BottomBar(
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete solve",
            )
        }
    }
}

@Composable
private fun TimeWithPenaltyLabel(
    solveTime: Long,
    penalty: PenaltyType?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Text(
            text = TimeFormatter.formatMilliseconds(solveTime),
            fontWeight = FontWeight.Bold,
            style = Typography.headlineSmall,
            modifier = Modifier.alignByBaseline()
        )

        penalty?.let {
            Text(
                text = penalty.asString(),
                color = Color.Red,
                style = Typography.labelSmall,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}

@Composable
private fun SolvedAtLabel(
    solvedAt: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.DateRange,
            contentDescription = "Solved at",
        )
        Column {
            Text(
                text = solvedAt.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                style = Typography.labelSmall,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = solvedAt.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
                style = Typography.labelSmall,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSolveDetailDialog() {
    SolveDetailsDialog(
        solve = Solve(
            id = 1L,
            scramble = "B2 R U L B' F' U F B U2 F D R' F' U B2 R2 B2 L R2 U D' R' L D'",
            time = 1000L,
            penalty = PenaltyType.DNF,
            createdAt = LocalDateTime.now(),
        ),
        scrambleImage = Svg(Dimension(0, 0)),
        onDeleteClick = {},
        onDismissRequest = {},
    )
}