package com.cassnyo.cuby.chronometer.scramblegenerator.di

import com.cassnyo.cuby.chronometer.scramblegenerator.ScrambleGenerator
import com.cassnyo.cuby.chronometer.scramblegenerator.ScrambleGeneratorImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle
import org.worldcubeassociation.tnoodle.scrambles.Puzzle

@Module
@InstallIn(SingletonComponent::class)
object PuzzleModule {

    @Provides
    fun providePuzzle(): Puzzle = ThreeByThreeCubePuzzle()

}

@Module
@InstallIn(SingletonComponent::class)
interface ScrambleGeneratorModule {

    @Binds
    fun bindScrambleGenerator(scrambleGenerator: ScrambleGeneratorImpl): ScrambleGenerator

}