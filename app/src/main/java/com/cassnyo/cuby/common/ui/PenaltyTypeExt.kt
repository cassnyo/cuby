package com.cassnyo.cuby.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cassnyo.cuby.R
import com.cassnyo.cuby.data.repository.solves.model.PenaltyType

@Composable
fun PenaltyType.asString() =
    when (this) {
        PenaltyType.DNF -> stringResource(id = R.string.common_penalty_dnf)
        PenaltyType.PLUS_TWO -> stringResource(id = R.string.common_penalty_plus_two)
    }