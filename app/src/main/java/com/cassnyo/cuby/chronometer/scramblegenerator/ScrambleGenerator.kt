package com.cassnyo.cuby.chronometer.scramblegenerator

interface ScrambleGenerator {
    suspend fun generate(): Scramble
}