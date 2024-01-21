package com.cassnyo.cuby.stopwatch.scramblegenerator

interface ScrambleGenerator {
    suspend fun generate(): Scramble
}