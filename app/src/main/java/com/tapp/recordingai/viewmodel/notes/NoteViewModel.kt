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
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

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

    var notesSearchQuery by mutableStateOf("")
        private set

    fun onNotesSearchQueryChange(value: String) {
        notesSearchQuery = value
    }

    fun filteredStorageNotes(): List<Note> {
        val q = notesSearchQuery.trim()
        if (q.isEmpty()) return notes
        return notes.filter { note -> note.matchesStorageSearch(q) }
    }

    private val structureJobs = ConcurrentHashMap<Int, Job>()

    fun changeTitle(value: String) {
        title = value
    }

    fun changeText(value: String) {
        text = value
    }

    suspend fun addNoteAndReturn(note: Note): Note {
        val id = noteDao.insertNote(note)
        val saved = note.copy(id = id.toInt())
        return saved
    }

    fun structureNoteWithAi(noteId: Int) {
        structureJobs[noteId]?.cancel()
        val job = viewModelScope.launch {
            val originalNote = withContext(Dispatchers.IO) {
                noteDao.getNoteById(noteId)
            } ?: return@launch

            withContext(Dispatchers.IO) {
                noteDao.updateNote(
                    originalNote.copy(status = NoteStatus.AI_PROCESSING)
                )
            }
            loadAllNotes()

            val finalText = try {
                withTimeout(120_000) {
                    aiService.structureText(originalNote.text)
                }
            } catch (_: TimeoutCancellationException) {
                originalNote.text
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("NoteViewModel", "structureText failed", e)
                originalNote.text
            }

            val latest = withContext(Dispatchers.IO) {
                noteDao.getNoteById(noteId)
            } ?: return@launch

            withContext(Dispatchers.IO) {
                noteDao.updateNote(
                    latest.copy(
                        text = finalText,
                        status = NoteStatus.NORMAL
                    )
                )
            }

            loadAllNotes()
        }
        structureJobs[noteId] = job
        job.invokeOnCompletion {
            structureJobs.remove(noteId, job)
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
        structureJobs[note.id]?.cancel()
        structureJobs.remove(note.id)
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

private fun Note.matchesStorageSearch(query: String): Boolean {
    val q = query.trim()
    if (q.isEmpty()) return true
    if (noteName.contains(q, ignoreCase = true)) return true
    if (text.contains(q, ignoreCase = true)) return true
    if (noteName.isBlank()) {
        if ("Заметка $id".contains(q, ignoreCase = true)) return true
        if ("Note $id".contains(q, ignoreCase = true)) return true
    }
    return false
}
