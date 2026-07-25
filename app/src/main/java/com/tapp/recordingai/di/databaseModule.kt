package com.tapp.recordingai.di

import com.tapp.recordingai.model.db.AppDatabase
import org.koin.dsl.module

val databaseModule = module {

    single {
        AppDatabase.getInstance(get())
    }

    single {
        get<AppDatabase>().noteDao()
    }

    single {
        get<AppDatabase>().folderDao()
    }

    single {
        get<AppDatabase>().deletedNoteDao()
    }

    single {
        get<AppDatabase>().recordStateDao()
    }
}
