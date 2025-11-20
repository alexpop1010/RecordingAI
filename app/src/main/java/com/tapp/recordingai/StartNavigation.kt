package com.tapp.recordingai


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
                    selected = navController.currentDestination?.route == "recording",
                    onClick = { navController.navigate("recording") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.microsvg),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("Запись") }
                )

                NavigationBarItem(
                    selected = navController.currentDestination?.route == "storage",
                    onClick = { navController.navigate("storage") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.stor),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("Хранилище") }
                )

                NavigationBarItem(
                    selected = navController.currentDestination?.route == "settings",
                    onClick = { navController.navigate("settings") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.stor),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    },
                    label = { Text("Настройки") }
                )
            }

        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "recording",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("recording") { Recording() }
            composable("storage") { Storage() }
            composable("settings") { Settings() }
        }

    }
}

