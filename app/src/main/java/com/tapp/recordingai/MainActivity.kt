package com.tapp.recordingai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tapp.recordingai.navigation.StartNavigation
import com.tapp.recordingai.recording.RecordingViewModel
import com.tapp.recordingai.ui.theme.RecordingAITheme

class MainActivity : ComponentActivity() {
    private val recViewModel: RecordingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("SR_DEBUG", "REQUESTING PERMISSION")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1001
            )
        }
        setContent {
            RecordingAITheme {
                StartNavigation(recViewModel)
            }

        }
    }

    override fun onPause() {
        super.onPause()
        recViewModel.pausingRecord()

    }
}