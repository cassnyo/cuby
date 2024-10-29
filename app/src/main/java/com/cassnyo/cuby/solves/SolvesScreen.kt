package com.cassnyo.cuby.solves

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SolvesScreen(
    modifier: Modifier = Modifier
) {
    Box {
        Text(text = "Solves", modifier = Modifier.align(Alignment.Center))
    }
}