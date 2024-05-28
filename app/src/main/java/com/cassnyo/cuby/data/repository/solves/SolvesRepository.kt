package com.cassnyo.cuby.data.repository.solves

import com.cassnyo.cuby.data.database.dao.SolveDao
import com.cassnyo.cuby.data.database.entity.SolveEntity
import com.cassnyo.cuby.data.repository.solves.mapper.toData
import com.cassnyo.cuby.data.repository.solves.mapper.toDomain
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType
import com.cassnyo.cuby.data.repository.solves.model.Solve
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

interface SolvesRepository {
    suspend fun saveSolve(scramble: String, time: Long): Solve
    suspend fun deleteSolve(solveId: Long)
    suspend fun setPenaltyToSolve(solveId: Long, penalty: PenaltyType)
}

class SolvesRepositoryImpl(
    private val solveDao: SolveDao,
    private val ioDispatcher: CoroutineDispatcher,
) : SolvesRepository {

    override suspend fun saveSolve(scramble: String, time: Long): Solve = withContext(ioDispatcher) {
        val solve = SolveEntity(
            scramble = scramble,
            time = time,
            penalty = null,
            createdAt = LocalDateTime.now(),
        )
        val id = solveDao.saveSolve(solve)
        solve.copy(id = id).toDomain()
    }

    override suspend fun deleteSolve(solveId: Long) = withContext(ioDispatcher) {
        solveDao.deleteSolve(solveId)
    }

    override suspend fun setPenaltyToSolve(solveId: Long, penalty: PenaltyType) =
        withContext(ioDispatcher) {
            solveDao.setPenalty(solveId, penalty.toData())
        }
}
