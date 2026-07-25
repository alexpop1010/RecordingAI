package com.tapp.recordingai.viewmodel.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.model.db.DeletedNote
import com.tapp.recordingai.model.db.DeletedNoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeletedNotesViewModel(private val dao: DeletedNoteDao) : ViewModel() {
    var notes by mutableStateOf<List<DeletedNote>>(emptyList())
        private set

    fun loadNotes() {
        viewModelScope.launch {
            clearExpiredNotes()
            notes = withContext(Dispatchers.IO) {
                dao.getAll()
            }
        }
    }

    fun deleteForever(note: DeletedNote) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(note)
            loadNotes()
        }
    }
    private suspend fun clearExpiredNotes() {
        val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
        val expireTime = System.currentTimeMillis() - thirtyDaysMillis
        dao.deleteOlderThan(expireTime)
    }
}