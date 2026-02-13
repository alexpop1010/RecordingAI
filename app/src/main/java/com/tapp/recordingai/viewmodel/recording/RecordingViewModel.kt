package com.tapp.recordingai.viewmodel.recording

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.model.db.RecordState
import com.tapp.recordingai.model.db.RecordStateDao
import com.tapp.recordingai.model.speech.Recognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordingViewModel(
    private val recordStateDao: RecordStateDao
) : ViewModel() {
    private var recognizer: Recognizer? = null

    fun initRecognizer(context: Context) {
        if (recognizer != null) return

        recognizer = Recognizer(
            context = context,
            onFinal = { result ->
                text += if (text.isBlank()) result else " $result"
            }
        )
    }

    var text by mutableStateOf("")
        private set

    var isEditable by mutableStateOf(false)
        private set

    var isRecording by mutableStateOf(false)
        private set

    private var saveJob: Job? = null

    fun startRecording() {
        isRecording = true
        isEditable = true
        recognizer?.start()
    }

    fun stopRecording() {
        isRecording = false
        isEditable = false
        recognizer?.stop()
    }

    fun onTextChanged(value: String) {
        text = value
        saveJob?.cancel()

        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(5_000)
            recordStateDao.saveText(RecordState(text = text))
        }
    }

    fun loadSavedText() {
        viewModelScope.launch {
            text = withContext(Dispatchers.IO) {
                recordStateDao.getText()?.text.orEmpty()
            }
        }
    }

    fun clearSavedText() {
        viewModelScope.launch(Dispatchers.IO) {
            recordStateDao.clearText()
        }
    }
    private var isInitialized = false

    fun loadSavedTextIfNeeded() {
        if (isInitialized) return

        viewModelScope.launch {
            val saved = withContext(Dispatchers.IO) {
                recordStateDao.getText()
            }

            if (!saved?.text.isNullOrBlank()) {
                text = saved!!.text
            }

            isInitialized = true
        }
    }


    override fun onCleared() {
        recognizer?.release()
        super.onCleared()
    }
}
