package com.cassnyo.cuby.chronometer.scramblegenerator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.worldcubeassociation.tnoodle.scrambles.Puzzle
import org.worldcubeassociation.tnoodle.svglite.Color
import javax.inject.Inject

class ScrambleGeneratorImpl @Inject constructor(
    private val puzzle: Puzzle,
) : ScrambleGenerator {

    override suspend fun generate(): Scramble = withContext(Dispatchers.Default) {
        val scramble = puzzle.generateScramble()
        val svg = puzzle.drawScramble(scramble, scrambleColors).apply {
            setStroke(STROKE_WIDTH_PX, 0, STROKE_JOIN)
        }

        Scramble(
            moves = scramble,
            image = svg.toString(),
        )
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