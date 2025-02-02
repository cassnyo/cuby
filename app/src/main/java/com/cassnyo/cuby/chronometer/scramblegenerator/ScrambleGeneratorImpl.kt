package com.cassnyo.cuby.chronometer.scramblegenerator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.worldcubeassociation.tnoodle.scrambles.Puzzle
import javax.inject.Inject

class ScrambleGeneratorImpl @Inject constructor(
    private val puzzle: Puzzle,
    private val scrambleImageGenerator: ScrambleImageGenerator,
) : ScrambleGenerator {

    override suspend fun generate(): Scramble = withContext(Dispatchers.IO) {
        val scramble = puzzle.generateScramble()
        val scrambleSvg = scrambleImageGenerator.generateImage(scramble)

        Scramble(
            moves = scramble,
            image = scrambleSvg.toString(),
        )
    }

}