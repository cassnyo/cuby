package com.cassnyo.cuby.data

import com.cassnyo.cuby.data.database.entity.SolveEntity
import kotlinx.coroutines.flow.Flow

interface TimesRepository {
    suspend fun saveSolve(solve: SolveEntity): SolveEntity
    fun observeTimes(): Flow<List<SolveEntity>>
    fun observeAverageOf(count: Int): Flow<Long>
}