package com.tapp.recordingai.viewmodel.recording

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.model.db.RecordState
import com.tapp.recordingai.model.db.RecordStateDao
import com.tapp.recordingai.model.speech.Recognizer
import com.tapp.recordingai.service.VoiceDictationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordingViewModel(
    private val recordStateDao: RecordStateDao,
    private val app: Application
) : ViewModel() {
    private var recognizer: Recognizer? = null
    var text by mutableStateOf("")
        private set

    private var speechStablePrefix = ""
    private var speechPartial = ""

    private fun joinParts(a: String, b: String): String {
        if (a.isBlank()) return b
        if (b.isBlank()) return a
        return "$a $b"
    }

    private fun refreshDisplayedText() {
        text = joinParts(speechStablePrefix, speechPartial)
        persistTextDebounced()
    }

    private var saveJob: Job? = null

    private fun persistTextDebounced() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(5_000)
            recordStateDao.saveText(RecordState(text = text))
        }
    }

    fun initRecognizer(context: Context) {
        if (recognizer != null) return

        recognizer = Recognizer(
            context = context.applicationContext,
            onPartial = { partial ->
                if (!isRecording) return@Recognizer
                speechPartial = partial
                refreshDisplayedText()
            },
            onFinal = { result ->
                if (!isRecording) return@Recognizer
                if (result.isNotBlank()) {
                    speechStablePrefix = joinParts(speechStablePrefix, result)
                }
                speechPartial = ""
                refreshDisplayedText()
            }
        )
    }

    var isEditable by mutableStateOf(false)
        private set

    var isRecording by mutableStateOf(false)
        private set

    fun startRecording() {
        speechPartial = ""
        speechStablePrefix = text
        isRecording = true
        isEditable = true
        VoiceDictationService.start(app)
        recognizer?.start()
    }

    fun stopRecording() {
        isRecording = false
        isEditable = false
        if (speechPartial.isNotBlank()) {
            speechStablePrefix = joinParts(speechStablePrefix, speechPartial)
            speechPartial = ""
            text = speechStablePrefix
            persistTextDebounced()
        }
        recognizer?.stop()
        VoiceDictationService.stop(app)
    }

    fun onTextChanged(value: String) {
        if (isRecording) {
            speechStablePrefix = value
            speechPartial = ""
        }
        text = value
        persistTextDebounced()
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
        VoiceDictationService.stop(app)
        super.onCleared()
    }
}
