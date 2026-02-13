package com.tapp.recordingai.model.db



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_notes")
data class DeletedNote(
    @PrimaryKey
    val id: Int,
    val noteName: String,
    val text: String,
    val deletedAt: Long
)
