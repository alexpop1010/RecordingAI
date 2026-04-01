package com.tapp.recordingai.di

import android.app.Application
import com.tapp.recordingai.viewmodel.notes.DeletedNotesViewModel
import com.tapp.recordingai.viewmodel.notes.NoteViewModel
import com.tapp.recordingai.viewmodel.recording.RecordingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        NoteViewModel(
            noteDao = get(),
            deletedNoteDao = get(),
            aiService = get()
        )
    }

    viewModel {
        DeletedNotesViewModel(
            dao = get()
        )
    }

    viewModel {
        RecordingViewModel(
            recordStateDao = get(),
            app = androidContext() as Application
        )
    }
}
