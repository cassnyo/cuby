package com.cassnyo.cuby.data

import com.cassnyo.cuby.data.database.entity.PenaltyTypeEntity
import com.cassnyo.cuby.data.database.entity.SolveEntity
import kotlinx.coroutines.flow.Flow

interface SolvesRepository {
    suspend fun saveSolve(solve: SolveEntity): SolveEntity
    suspend fun deleteSolve(solveId: Long)
    suspend fun setPenaltyToSolve(solveId: Long, penalty: PenaltyTypeEntity)
    fun observeTimes(): Flow<List<SolveEntity>>
    fun observeAverageOf(count: Int): Flow<Long>
}