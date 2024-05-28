package com.cassnyo.cuby.data.repository.solves.mapper

import com.cassnyo.cuby.data.database.entity.PenaltyTypeEntity
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType

fun PenaltyTypeEntity.toDomain() =
    when (this) {
        PenaltyTypeEntity.DNF -> PenaltyType.DNF
        PenaltyTypeEntity.PLUS_TWO -> PenaltyType.PLUS_TWO
    }

fun PenaltyType.toData() =
    when (this) {
        PenaltyType.DNF -> PenaltyTypeEntity.DNF
        PenaltyType.PLUS_TWO -> PenaltyTypeEntity.PLUS_TWO
    }