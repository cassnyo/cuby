package com.cassnyo.cuby.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cassnyo.cuby.chronometer.ChronometerScreen
import com.cassnyo.cuby.solves.SolvesScreen
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Chronometer : Screen()

    @Serializable
    data object Solves : Screen()
}

@Composable
fun CubyNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Screen.Chronometer> {
            ChronometerScreen()
        }
        composable<Screen.Solves> {
            SolvesScreen()
        }
    }
}