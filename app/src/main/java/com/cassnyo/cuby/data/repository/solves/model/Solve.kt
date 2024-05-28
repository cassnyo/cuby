package com.cassnyo.cuby.data.repository.solves.model

import java.time.LocalDateTime

data class Solve(
    val id: Long,
    val scramble: String,
    val time: Long,
    val penalty: PenaltyType?,
    val createdAt: LocalDateTime,
)