package com.cassnyo.cuby.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cassnyo.cuby.ui.navigation.CubyNavGraph
import com.cassnyo.cuby.ui.navigation.Screen
import com.cassnyo.cuby.ui.theme.CubyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MainScreen()
        }
    }

    @Composable
    private fun MainScreen() {
        val navController = rememberNavController()
        val currentDestination: Screen by remember { mutableStateOf(Screen.Chronometer) }

        CubyTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentDestination == Screen.Chronometer,
                            onClick = { navController.navigate(Screen.Chronometer) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "Chronometer",
                                )
                            }
                        )
                        NavigationBarItem(
                            selected = currentDestination == Screen.Solves,
                            onClick = { navController.navigate(Screen.Solves) },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.List,
                                    contentDescription = "Solves",
                                )
                            }
                        )

                    }
                }
            ) { paddingValues ->
                CubyNavGraph(
                    navController = navController,
                    startDestination = currentDestination,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

    }
}