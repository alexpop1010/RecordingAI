package com.tapp.recordingai

import android.app.Application
import com.tapp.recordingai.di.aiModule
import com.tapp.recordingai.di.databaseModule
import com.tapp.recordingai.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class RecordingAiApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RecordingAiApp)
            modules(
                databaseModule,
                aiModule,
                viewModelModule
            )
        }
    }
}
