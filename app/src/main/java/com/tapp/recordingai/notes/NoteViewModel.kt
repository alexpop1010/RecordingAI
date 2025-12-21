package com.tapp.recordingai.notes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.db.AppDatabase
import com.tapp.recordingai.db.Note
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {

    private val dao = AppDatabase.Companion.getInstance(application).noteDao()

    var title by mutableStateOf("")
    var text by mutableStateOf("")
    var notes by mutableStateOf(listOf<Note>())

    suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)
    }
    fun showNote(note: Note?){
        title = if (note?.noteName!!.isNotBlank()) note.noteName else "No title"
        text = note?.text?:"No text"
    }
    fun updateNote(id: Int) {
        viewModelScope.launch {
            val note = dao.getNoteById(id) ?: return@launch
            val updatedNote = note.copy(
                noteName = title,
                text = text
            )
            dao.updateNote(updatedNote)
            loadAllNotes()
        }
    }
    fun deleteNote(note:Note){
        viewModelScope.launch {
            dao.deleteNote(note)
            notes = dao.getAll()
        }
    }
    fun addNote(note: Note) {
        viewModelScope.launch {
            dao.insertNote(note)
        }
    }
    fun loadAllNotes() {
        viewModelScope.launch {
            val fromDb = dao.getAll()
            notes = fromDb
        }
    }
}