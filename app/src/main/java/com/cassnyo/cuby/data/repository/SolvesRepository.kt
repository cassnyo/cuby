package com.cassnyo.cuby.data.repository

import com.cassnyo.cuby.data.database.dao.SolveDao
import com.cassnyo.cuby.data.database.entity.PenaltyTypeEntity
import com.cassnyo.cuby.data.database.entity.SolveEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SolvesRepository {
    suspend fun saveSolve(solve: SolveEntity): SolveEntity
    suspend fun deleteSolve(solveId: Long)
    suspend fun setPenaltyToSolve(solveId: Long, penalty: PenaltyTypeEntity)
}

class SolvesRepositoryImpl(
    private val solveDao: SolveDao,
    private val ioDispatcher: CoroutineDispatcher,
) : SolvesRepository {

    override suspend fun saveSolve(solve: SolveEntity): SolveEntity = withContext(ioDispatcher) {
        val id = solveDao.saveSolve(solve)
        solve.copy(id = id)
    }

    override suspend fun deleteSolve(solveId: Long) = withContext(ioDispatcher) {
        solveDao.deleteSolve(solveId)
    }

    override suspend fun setPenaltyToSolve(solveId: Long, penalty: PenaltyTypeEntity) =
        withContext(ioDispatcher) {
            solveDao.setPenalty(solveId, penalty)
        }
}
