package com.tapp.recordingai.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.tapp.recordingai.DeletedScreen
import com.tapp.recordingai.NavConstants
import com.tapp.recordingai.notes.NoteScreen
import com.tapp.recordingai.R
import com.tapp.recordingai.recording.Recording
import com.tapp.recordingai.settings.Settings
import com.tapp.recordingai.notes.Storage
import com.tapp.recordingai.recording.RecordingViewModel

@Composable
fun StartNavigation(recordingViewModel: RecordingViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentRoute == NavConstants.RECORDING,
                    onClick = {
                        navController.navigate(NavConstants.RECORDING) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.microsvg),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text(stringResource(R.string.record)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == NavConstants.STORAGE,
                    onClick = {
                        navController.navigate(NavConstants.STORAGE) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.stor),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text(stringResource(R.string.storage)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == NavConstants.SETTINGS,
                    onClick = {
                        navController.navigate(NavConstants.SETTINGS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.stor),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text(stringResource(R.string.settings)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedIconColor = Color.Black,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavConstants.RECORDING,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavConstants.RECORDING) { Recording(viewModel = recordingViewModel) }
            composable(NavConstants.STORAGE) { Storage(navController) }
            composable(NavConstants.SETTINGS) { Settings(navController) }
            composable(NavConstants.NOTE) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")!!.toInt()
                NoteScreen(id, onBack = { navController.popBackStack() })
            }
            composable(NavConstants.DELETED){ DeletedScreen(navController) }
        }
    }
}
