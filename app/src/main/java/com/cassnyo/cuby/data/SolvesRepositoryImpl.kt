package com.cassnyo.cuby.data

import com.cassnyo.cuby.data.database.CubyDatabase
import com.cassnyo.cuby.data.database.entity.PenaltyTypeEntity
import com.cassnyo.cuby.data.database.entity.SolveEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SolvesRepositoryImpl(
    private val database: CubyDatabase,
    private val ioDispatcher: CoroutineDispatcher,
) : SolvesRepository {

    override suspend fun saveSolve(solve: SolveEntity): SolveEntity = withContext(ioDispatcher) {
        val id = database.solveDao().saveSolve(solve)
        solve.copy(id = id)
    }

    override suspend fun deleteSolve(solveId: Long) = withContext(ioDispatcher) {
        database.solveDao().deleteSolve(solveId)
    }

    override suspend fun setPenaltyToSolve(solveId: Long, penalty: PenaltyTypeEntity) {
        database.solveDao().setPenalty(solveId, penalty)
    }

    override fun observeTimes(): Flow<List<SolveEntity>> = database.solveDao().observeSolves()

    override fun observeAverageOf(count: Int): Flow<Long> =
        observeTimes()
            .map { times ->
                if (times.size < count) {
                    0L
                } else {
                    times.takeLast(count).map { it.time }.sum() / count
                }
            }
            .flowOn(ioDispatcher)

}