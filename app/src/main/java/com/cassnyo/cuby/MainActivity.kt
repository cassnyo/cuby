package com.cassnyo.cuby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cassnyo.cuby.stopwatch.Chronometer
import com.cassnyo.cuby.stopwatch.ChronometerScreen
import com.cassnyo.cuby.stopwatch.ChronometerViewModel
import com.cassnyo.cuby.ui.theme.CubyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CubyTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChronometerScreen(
                        viewModel = ChronometerViewModel(Chronometer())
                    )
                }
            }
        }
    }
}