package com.tapp.recordingai.notes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tapp.recordingai.db.AppDatabase
import com.tapp.recordingai.db.DeletedNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeletedNotesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase
        .getInstance(application)
        .deletedNoteDao()

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
