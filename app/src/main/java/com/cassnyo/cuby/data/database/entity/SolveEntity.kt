package com.cassnyo.cuby.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Solve")
data class SolveEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scramble: String,
    val time: Long,
    // TODO Add creation date
)