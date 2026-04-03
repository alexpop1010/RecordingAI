package com.tapp.recordingai.model.ai

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class OpenAiService(
    private val client: OpenAiClient = OpenAiClient()
) {

    companion object {
        /** Параллельных запросов структуризации — чтобы не упираться в лимиты и не грузить память. */
        private const val STRUCTURE_CONCURRENCY = 3
    }

    suspend fun structureText(text: String): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return ""

        val chunks = TextChunker.split(trimmed)
        if (chunks.isEmpty()) return ""

        val structuredNotes = if (chunks.size == 1) {
            listOf(
                client.call(
                    PromptFactory.structureChunk(
                        text = chunks.single(),
                        partIndex = 1,
                        totalParts = 1
                    )
                )
            )
        } else {
            val semaphore = Semaphore(STRUCTURE_CONCURRENCY)
            coroutineScope {
                chunks.mapIndexed { index, chunk ->
                    async {
                        semaphore.withPermit {
                            client.call(
                                PromptFactory.structureChunk(
                                    text = chunk,
                                    partIndex = index + 1,
                                    totalParts = chunks.size
                                )
                            )
                        }
                    }
                }.awaitAll()
            }
        }

        return MergingText.reduce(structuredNotes, client)
    }
}
