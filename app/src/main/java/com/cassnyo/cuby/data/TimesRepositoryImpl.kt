package com.cassnyo.cuby.data

import com.cassnyo.cuby.data.model.Time
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TimesRepositoryImpl : TimesRepository {

    private val times = mutableListOf<Time>()
    private val timesFlow = MutableSharedFlow<List<Time>>()
    override suspend fun saveTime(time: Time): Time {
        times.add(time)
        timesFlow.emit(times)
        return time
    }

    override fun observeTimes(): Flow<List<Time>> = timesFlow

    override fun observeAverageOf(count: Int): Flow<Long> =
        observeTimes()
            .map { times ->
                if (times.size < count) {
                    0L
                } else {
                    times.takeLast(count).map { it.time }.sum() / count
                }
            }

}