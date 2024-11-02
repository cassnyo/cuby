package com.cassnyo.cuby.data.repository.solves

import com.cassnyo.cuby.common.di.IoDispatcher
import com.cassnyo.cuby.data.database.dao.SolveDao
import com.cassnyo.cuby.data.database.entity.SolveEntity
import com.cassnyo.cuby.data.repository.solves.mapper.toData
import com.cassnyo.cuby.data.repository.solves.mapper.toDomain
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType
import com.cassnyo.cuby.data.repository.solves.model.Solve
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

interface SolvesRepository {
    fun observeSolve(solveId: Long): Flow<Solve?>
    suspend fun saveSolve(scramble: String, time: Long): Solve
    suspend fun deleteSolve(solveId: Long)
    suspend fun setPenaltyToSolve(solveId: Long, penalty: PenaltyType)
}

class SolvesRepositoryImpl @Inject constructor(
    private val solveDao: SolveDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SolvesRepository {

    override fun observeSolve(solveId: Long): Flow<Solve?> =
        solveDao
            .observeSolve(solveId).map { it?.toDomain() }
            .flowOn(ioDispatcher)

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
