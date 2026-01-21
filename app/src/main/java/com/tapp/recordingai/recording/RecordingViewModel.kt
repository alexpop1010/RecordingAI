package com.tapp.recordingai.recording

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tapp.recordingai.db.AppDatabase
import com.tapp.recordingai.db.RecordState
import com.tapp.recordingai.speech.Recognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class RecordingViewModel(application: Application): AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).recordStateDao()
    private var job: Job? = null
    private var recognizer: Recognizer? = null


    var text: String by mutableStateOf("")
    var isEditable by mutableStateOf(false)
    var isRecording by mutableStateOf(false)

    fun initRecognizer(context: Context) {
        if (recognizer != null) return

        recognizer = Recognizer(
            context = context,
            onFinal = { result ->
                text += if (text.isBlank()) result else " $result"
            }
        )
    }
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



    fun textChanged(value:String){
        text = value
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(5000)
            dao.saveText(RecordState(text = text))
        }
    }
    fun deleteEditText(){
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearText()
        }
    }
    fun getEditText(){
        viewModelScope.launch{
            val editText = withContext(Dispatchers.IO) {
                dao.getText()
            }
            text = editText?.text ?:""

        }
    }
    fun pausingRecord() {
        if (isRecording) {
            stopRecording()
        }
    }

    override fun onCleared() {
        super.onCleared()
        recognizer?.release()
    }

}