package com.tapp.recordingai.viewmodel.notes

sealed interface StorageBrowse {
    data object Root : StorageBrowse

    data class NotesIn(val folderId: Int) : StorageBrowse
}
