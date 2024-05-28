package com.cassnyo.cuby.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cassnyo.cuby.data.database.CubyDatabase
import com.cassnyo.cuby.data.repository.solves.SolvesRepositoryImpl
import com.cassnyo.cuby.data.repository.statistics.StatisticsRepositoryImpl
import com.cassnyo.cuby.chronometer.Chronometer
import com.cassnyo.cuby.chronometer.ChronometerScreen
import com.cassnyo.cuby.chronometer.ChronometerViewModel
import com.cassnyo.cuby.chronometer.scramblegenerator.ScrambleGeneratorImpl
import com.cassnyo.cuby.ui.theme.CubyTheme
import kotlinx.coroutines.Dispatchers
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        val database = CubyDatabase.getInstance(this)
        val viewModel = ChronometerViewModel(
            chronometer = Chronometer(),
            scrambleGenerator = ScrambleGeneratorImpl(
                puzzle = ThreeByThreeCubePuzzle()
            ),
            solvesRepository = SolvesRepositoryImpl(
                solveDao = database.solveDao(),
                ioDispatcher = Dispatchers.IO,
            ),
            statisticsRepository = StatisticsRepositoryImpl(
                solveDao = database.solveDao(),
                ioDispatcher = Dispatchers.IO,
            ),
        )

        setContent {
            CubyTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChronometerScreen(viewModel = viewModel)
                }
            }
        }
    }
}