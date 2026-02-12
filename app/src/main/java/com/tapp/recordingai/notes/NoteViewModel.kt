package com.tapp.recordingai.notes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.ai.OpenAiService
import com.tapp.recordingai.db.AppDatabase
import com.tapp.recordingai.db.DeletedNote
import com.tapp.recordingai.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(application: Application): AndroidViewModel(application) {


    private val dao = AppDatabase.Companion.getInstance(application).noteDao()
    private val OpenAiService = OpenAiService()

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

    suspend fun addNoteAndReturn(note: Note): Note {
        android.util.Log.d(
            "DEBUG_FLOW",
            "addNoteAndReturn INPUT text='${note.text}'"
        )

        val id = dao.insertNote(note)

        val saved = note.copy(id = id.toInt())

        android.util.Log.d(
            "DEBUG_FLOW",
            "addNoteAndReturn OUTPUT text='${saved.text}'"
        )

        return saved
    }

    fun structureNoteWithAi(noteId: Int) {
        viewModelScope.launch {


            val originalNote = withContext(Dispatchers.IO) {
                dao.getNoteById(noteId)
            } ?: return@launch
            android.util.Log.d(
                "DEBUG_FLOW",
                "Note FROM DB before AI: id=${originalNote.id}, text='${originalNote.text}'"
            )


            withContext(Dispatchers.IO) {
                dao.updateNote(originalNote.copy(status = NoteStatus.AI_PROCESSING))
            }
            loadAllNotes()

            try {

                val structuredText = OpenAiService.structureText(originalNote.text)
                android.util.Log.d(
                    "DEBUG_FLOW",
                    "AI RESULT text='${structuredText}'"
                )

                withContext(Dispatchers.IO) {
                    dao.updateNote(
                        originalNote.copy(
                            text = structuredText,
                            status = NoteStatus.NORMAL
                        )
                    )
                }
                loadAllNotes()

            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    dao.updateNote(originalNote.copy(status = NoteStatus.NORMAL))

                }
                loadAllNotes()
            }
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            val deletedNote = DeletedNote(
                id = note.id,
                noteName = note.noteName,
                text = note.text,
                deletedAt = System.currentTimeMillis()
            )
            AppDatabase
                .getInstance(getApplication())
                .deletedNoteDao()
                .insert(deletedNote)

            dao.deleteNote(note)

            loadAllNotes()
        }
    }



    suspend fun loadAllNotes() {
        val fromDb = withContext(Dispatchers.IO) {
            dao.getAll()
        }
        notes = fromDb
    }
}