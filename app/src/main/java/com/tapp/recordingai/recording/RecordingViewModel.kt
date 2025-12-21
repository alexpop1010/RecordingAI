package com.tapp.recordingai.recording

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RecordingViewModel: ViewModel() {

    var text by mutableStateOf("")
    var isEditable by mutableStateOf(false)
    var isRecording by mutableStateOf(false)

}