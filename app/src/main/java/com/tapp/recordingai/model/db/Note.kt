package com.tapp.recordingai.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tapp.recordingai.viewmodel.notes.NoteStatus

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var noteName: String = "",
    var text: String,
    val status: NoteStatus = NoteStatus.NORMAL
)


