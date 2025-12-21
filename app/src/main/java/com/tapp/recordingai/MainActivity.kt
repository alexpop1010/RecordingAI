package com.tapp.recordingai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tapp.recordingai.navigation.StartNavigation
import com.tapp.recordingai.ui.theme.RecordingAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordingAITheme {
                StartNavigation()
            }

        }
    }
}