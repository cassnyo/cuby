package com.cassnyo.cuby.data.repository.solves.mapper

import com.cassnyo.cuby.data.database.entity.SolveEntity
import com.cassnyo.cuby.data.repository.solves.model.Solve

fun SolveEntity.toDomain() =
    Solve(
        id = this.id,
        scramble = this.scramble,
        time = this.time,
        penalty = this.penalty?.toDomain(),
        createdAt = createdAt,
    )