package com.cassnyo.cuby.data.repository

import com.cassnyo.cuby.data.database.dao.SolveDao
import com.cassnyo.cuby.data.database.entity.SolveEntity
import com.cassnyo.cuby.data.repository.StatisticsRepository.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

interface StatisticsRepository {

    data class Statistics(
        val count: Int,
        val bestSolve: Long,
        val averageOf5: Long,
        val averageOf12: Long,
        val averageOf50: Long,
        val averageOf100: Long,
    )

    fun observeStatistics(): Flow<Statistics>
}

class StatisticsRepositoryImpl(
    private val solveDao: SolveDao,
    private val ioDispatcher: CoroutineDispatcher,
) : StatisticsRepository {

    @Suppress("UNCHECKED_CAST")
    override fun observeStatistics(): Flow<Statistics> =
        combine(
            solveDao.observeAllSolves(),
            solveDao.observeBestSolve(),
            solveDao.observeLastNSolves(count = 5),
            solveDao.observeLastNSolves(count = 12),
            solveDao.observeLastNSolves(count = 50),
            solveDao.observeLastNSolves(count = 100),
        ) { flows ->
            Statistics(
                count = (flows[0] as List<SolveEntity>).size,
                bestSolve = (flows[1] as SolveEntity).time,
                averageOf5 = (flows[2] as List<SolveEntity>).calculateAverageTimeOf(5),
                averageOf12 = (flows[3] as List<SolveEntity>).calculateAverageTimeOf(12),
                averageOf50 = (flows[4] as List<SolveEntity>).calculateAverageTimeOf(50),
                averageOf100 = (flows[5] as List<SolveEntity>).calculateAverageTimeOf(100),
            )
        }.flowOn(ioDispatcher)

    private fun List<SolveEntity>.calculateAverageTimeOf(count: Int): Long {
        if (size < count) return 0L
        return sumOf { it.time } / count
    }

}