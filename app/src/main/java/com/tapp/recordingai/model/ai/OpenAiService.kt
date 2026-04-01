package com.tapp.recordingai.model.ai



class OpenAiService(
    private val client: OpenAiClient = OpenAiClient()
) {

    suspend fun structureText(text: String): String {
        val structuredNotes = if (text.length <= TextChunker.MAX_CHARS) {
            listOf(
                client.call(
                    PromptFactory.structureChunk(text)
                )
            )
        } else {
            val chunks = TextChunker.split(text)
    chunks.mapIndexed { index, chunk ->
                client.call(
                    PromptFactory.structureChunk(chunk)
                )
            }
        }
        return MergingText.reduce(structuredNotes, client)
    }

}

