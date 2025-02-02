package com.cassnyo.cuby.chronometer.scramblegenerator

import android.content.Context
import androidx.window.layout.WindowMetricsCalculator
import dagger.hilt.android.qualifiers.ApplicationContext
import org.worldcubeassociation.tnoodle.scrambles.Puzzle
import org.worldcubeassociation.tnoodle.svglite.Color
import org.worldcubeassociation.tnoodle.svglite.Svg
import javax.inject.Inject

class ScrambleImageGeneratorImpl @Inject constructor(
    private val puzzle: Puzzle,
    @ApplicationContext private val context: Context,
) : ScrambleImageGenerator {

    private val windowMetricsCalculator by lazy { WindowMetricsCalculator.getOrCreate() }

    override fun generateImage(scramble: String): Svg {
        val thumbnailSizePx = calculateThumbnailSizePx()
        return puzzle.drawScramble(scramble, scrambleColors)
            .apply {
                setStroke(STROKE_WIDTH_PX, 0, STROKE_JOIN)
                setAttribute("width", "${thumbnailSizePx}px")
                setAttribute("height", "${thumbnailSizePx}px")
            }
    }

    private fun calculateThumbnailSizePx(): Int {
        val windowWidth = windowMetricsCalculator
            .computeCurrentWindowMetrics(context)
            .bounds.width()
        return windowWidth / 4
    }

    private companion object {
        val scrambleColors = mapOf(
            "B" to Color(99, 129, 184),
            "D" to Color(252, 230, 105),
            "F" to Color(103, 184, 165),
            "L" to Color(243, 164, 102),
            "R" to Color(237, 101, 105),
            "U" to Color(252, 252, 255),
        )

        const val STROKE_WIDTH_PX = 1
        const val STROKE_JOIN = "round"
    }
}