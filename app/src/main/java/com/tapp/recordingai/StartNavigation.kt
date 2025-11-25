package com.tapp.recordingai



import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun StartNavigation() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == NavConstants.RECORDING,
                    onClick = { navController.navigate(NavConstants.RECORDING) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.microsvg),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text(stringResource(R.string.record)) }
                )

                NavigationBarItem(
                    selected = navController.currentDestination?.route == NavConstants.STORAGE,
                    onClick = { navController.navigate(NavConstants.STORAGE) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.stor),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text(stringResource(R.string.storage)) }
                )

                NavigationBarItem(
                    selected = navController.currentDestination?.route == NavConstants.SETTINGS,
                    onClick = { navController.navigate(NavConstants.SETTINGS) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.stor),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text(stringResource(R.string.settings)) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavConstants.RECORDING,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavConstants.RECORDING) { Recording() }
            composable(NavConstants.STORAGE) { Storage() }
            composable(NavConstants.SETTINGS) { Settings() }
        }
    }
}

