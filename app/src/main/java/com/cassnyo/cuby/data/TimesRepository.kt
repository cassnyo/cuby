package com.cassnyo.cuby.data

import com.cassnyo.cuby.data.model.Time
import kotlinx.coroutines.flow.Flow

interface TimesRepository {
    suspend fun saveTime(time: Time): Time
    fun observeTimes(): Flow<List<Time>>
    fun observeAverageOf(count: Int): Flow<Long>
}