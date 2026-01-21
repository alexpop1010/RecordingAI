package com.tapp.recordingai.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecordState (
    @PrimaryKey val id: Int = 0,
    val text: String = ""
)

