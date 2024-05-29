package com.cassnyo.cuby.data.repository.di

import com.cassnyo.cuby.data.repository.solves.SolvesRepository
import com.cassnyo.cuby.data.repository.solves.SolvesRepositoryImpl
import com.cassnyo.cuby.data.repository.statistics.StatisticsRepository
import com.cassnyo.cuby.data.repository.statistics.StatisticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindSolvesRepository(repository: SolvesRepositoryImpl): SolvesRepository

    @Binds
    fun bindStatisticsRepository(repository: StatisticsRepositoryImpl): StatisticsRepository

}