package com.tapp.recordingai.di

import com.tapp.recordingai.model.ai.OpenAiClient
import com.tapp.recordingai.model.ai.OpenAiService
import org.koin.dsl.module

val aiModule = module {

    single {
        OpenAiClient()
    }

    single {
        OpenAiService(
            client = get()
        )
    }
}
