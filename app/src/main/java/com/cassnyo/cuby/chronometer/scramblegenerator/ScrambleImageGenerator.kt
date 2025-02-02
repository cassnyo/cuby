package com.cassnyo.cuby.chronometer.scramblegenerator

import org.worldcubeassociation.tnoodle.svglite.Svg

interface ScrambleImageGenerator {
    fun generateImage(scramble: String): Svg
}