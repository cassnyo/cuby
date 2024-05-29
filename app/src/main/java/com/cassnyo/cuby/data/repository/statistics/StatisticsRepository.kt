package com.cassnyo.cuby.data.repository.statistics

import com.cassnyo.cuby.common.di.IoDispatcher
import com.cassnyo.cuby.data.database.dao.SolveDao
import com.cassnyo.cuby.data.database.entity.SolveEntity
import com.cassnyo.cuby.data.repository.statistics.model.Statistics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface StatisticsRepository {
    fun observeStatistics(): Flow<Statistics>
}

class StatisticsRepositoryImpl @Inject constructor(
    private val solveDao: SolveDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : StatisticsRepository {

    private data class Averages(
        val averageOf5: Long,
        val averageOf12: Long,
        val averageOf50: Long,
        val averageOf100: Long,
    )

    override fun observeStatistics(): Flow<Statistics> =
        combine(
            solveDao.observeAllSolves(),
            solveDao.observeBestSolve(),
            observeAverages(),
        ) { allSolves, bestSolve, averages ->
            Statistics(
                count = allSolves.size,
                bestSolve = bestSolve?.time ?: 0L,
                median = allSolves.map { it.time }.median(),
                averageOf5 = averages.averageOf5,
                averageOf12 = averages.averageOf12,
                averageOf50 = averages.averageOf50,
                averageOf100 = averages.averageOf100,
            )
        }.flowOn(ioDispatcher)

    private fun List<SolveEntity>.calculateAverageTimeOf(count: Int): Long {
        if (size < count) return 0L
        return sumOf { it.time } / count
    }

    private fun observeAverages(): Flow<Averages> =
        combine(
            solveDao.observeLastNSolves(count = 5),
            solveDao.observeLastNSolves(count = 12),
            solveDao.observeLastNSolves(count = 50),
            solveDao.observeLastNSolves(count = 100)
        ) { ao5, ao12, ao50, ao100 ->
            Averages(
                averageOf5 = ao5.calculateAverageTimeOf(5),
                averageOf12 = ao12.calculateAverageTimeOf(12),
                averageOf50 = ao50.calculateAverageTimeOf(50),
                averageOf100 = ao100.calculateAverageTimeOf(100),
            )
        }
}

private fun List<Long>.median(): Long {
    val sorted = sorted()
    val middle = size / 2
    return if (size % 2 == 0) {
        (sorted[middle - 1] + sorted[middle]) / 2
    } else {
        sorted[middle]
    }
}
