package com.tapp.recordingai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tapp.recordingai.navigation.StartNavigation
import com.tapp.recordingai.ui.theme.RecordingAITheme
import com.tapp.recordingai.utils.LanguageSet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        LanguageSet.applySavedLanguage(this)
        super.onCreate(savedInstanceState)

        setContent {
            RecordingAITheme {
                StartNavigation()
            }
        }

    }
}
