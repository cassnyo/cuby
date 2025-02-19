package com.cassnyo.cuby.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cassnyo.cuby.data.database.entity.PenaltyTypeEntity
import com.cassnyo.cuby.data.database.entity.SolveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolveDao {

    @Query("SELECT * FROM solve WHERE id == :solveId")
    fun observeSolve(solveId: Long): Flow<SolveEntity?>

    @Insert
    suspend fun saveSolve(solve: SolveEntity): Long

    @Query("DELETE FROM solve WHERE id == :id")
    suspend fun deleteSolve(id: Long)

    @Query("UPDATE solve SET penalty = :penalty WHERE id = :id")
    suspend fun setPenalty(id: Long, penalty: PenaltyTypeEntity)

    @Query("SELECT * FROM solve ORDER BY createdAt DESC")
    fun observeAllSolves(): Flow<List<SolveEntity>>

    @Query("SELECT * FROM solve ORDER BY createdAt DESC LIMIT :count")
    fun observeLastNSolves(count: Int): Flow<List<SolveEntity>>

    @Query("SELECT * FROM solve ORDER BY time ASC LIMIT 1")
    fun observeBestSolve(): Flow<SolveEntity?>

}