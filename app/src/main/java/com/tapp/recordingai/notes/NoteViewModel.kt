package com.tapp.recordingai.notes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.db.AppDatabase
import com.tapp.recordingai.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(application: Application): AndroidViewModel(application) {

    private val dao = AppDatabase.Companion.getInstance(application).noteDao()

    var title: String by mutableStateOf("")
        private set
    var text: String by mutableStateOf("")
        private set
    var notes by mutableStateOf(listOf<Note>())

    fun changeTitle(value:String){
        title = value
    }
    fun changeText(texti: String){
        text = texti
    }

    suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)
    }
    fun showNote(note: Note?){
        title = if (note?.noteName!!.isNotBlank()) note.noteName else "Заметка " +note.id.toString()
        text = note?.text?:"..."
    }
    fun updateNote(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = dao.getNoteById(id) ?: return@launch
            val updatedNote = note.copy(
                noteName = title,
                text = text
            )
            dao.updateNote(updatedNote)
            loadAllNotes()
        }
    }
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            val updatedNotes = withContext(Dispatchers.IO) {
                dao.deleteNote(note)
                dao.getAll()
            }
            notes = updatedNotes
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertNote(note)
        }
    }
    fun loadAllNotes() {
        viewModelScope.launch {
            val fromDb = withContext(Dispatchers.IO) {
                dao.getAll()
            }
            notes = fromDb
        }
    }

}