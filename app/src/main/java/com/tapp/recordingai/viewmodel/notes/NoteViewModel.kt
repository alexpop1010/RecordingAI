package com.tapp.recordingai.viewmodel.notes

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.model.ai.OpenAiService
import com.tapp.recordingai.model.db.DeletedNote
import com.tapp.recordingai.model.db.DeletedNoteDao
import com.tapp.recordingai.model.db.Note
import com.tapp.recordingai.model.db.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(
    private val noteDao: NoteDao,
    private val deletedNoteDao: DeletedNoteDao,
    private val aiService: OpenAiService
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var text by mutableStateOf("")
        private set

    var notes by mutableStateOf<List<Note>>(emptyList())
        private set

    fun changeTitle(value: String) {
        title = value
    }

    fun changeText(value: String) {
        text = value
    }

    suspend fun addNoteAndReturn(note: Note): Note {
        Log.d("DEBUG_FLOW", "addNoteAndReturn INPUT text='${note.text}'")

        val id = noteDao.insertNote(note)
        val saved = note.copy(id = id.toInt())

        Log.d("DEBUG_FLOW", "addNoteAndReturn OUTPUT text='${saved.text}'")

        return saved
    }

    fun structureNoteWithAi(noteId: Int) {
        viewModelScope.launch {

            val originalNote = withContext(Dispatchers.IO) {
                noteDao.getNoteById(noteId)
            } ?: return@launch

            Log.d(
                "DEBUG_FLOW",
                "Note FROM DB before AI: id=${originalNote.id}, text='${originalNote.text}'"
            )

            withContext(Dispatchers.IO) {
                noteDao.updateNote(
                    originalNote.copy(status = NoteStatus.AI_PROCESSING)
                )
            }
            loadAllNotes()

            try {
                val structuredText = aiService.structureText(originalNote.text)

                Log.d("DEBUG_FLOW", "AI RESULT text='$structuredText'")

                withContext(Dispatchers.IO) {
                    noteDao.updateNote(
                        originalNote.copy(
                            text = structuredText,
                            status = NoteStatus.NORMAL
                        )
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    noteDao.updateNote(
                        originalNote.copy(status = NoteStatus.NORMAL)
                    )
                }
            }

            loadAllNotes()
        }
    }

    suspend fun getNoteById(id: Int): Note? =
        withContext(Dispatchers.IO) {
            noteDao.getNoteById(id)
        }

    fun showNote(note: Note?) {
        if (note == null) return

        title = if (note.noteName.isNotBlank()) {
            note.noteName
        } else {
            "Заметка ${note.id}"
        }

        text = note.text
    }

    fun updateNote(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = noteDao.getNoteById(id) ?: return@launch

            noteDao.updateNote(
                note.copy(
                    noteName = title,
                    text = text
                )
            )

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

            deletedNoteDao.insert(deletedNote)
            noteDao.deleteNote(note)

            loadAllNotes()
        }
    }

    suspend fun loadAllNotes() {
        notes = withContext(Dispatchers.IO) {
            noteDao.getAll()
        }
    }
}
