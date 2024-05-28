package com.cassnyo.cuby.data.repository.statistics.model

data class Statistics(
    val count: Int,
    val bestSolve: Long,
    val median: Long,
    val averageOf5: Long,
    val averageOf12: Long,
    val averageOf50: Long,
    val averageOf100: Long,
)