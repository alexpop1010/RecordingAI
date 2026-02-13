package com.tapp.recordingai.model.ai

object MergingText {

    private const val GROUP_SIZE = 5

    suspend fun reduce(
        chunks: List<String>,
        client: OpenAiClient
    ): String {

        var notesToMerge = chunks

        while (notesToMerge.size > 1) {

            val mergedNotes = mutableListOf<String>()
            val groups = notesToMerge.chunked(GROUP_SIZE)

            for ((index, group) in groups.withIndex()) {
                val merged = client.call(
                    PromptFactory.mergeChunks(group)
                )
                mergedNotes += merged
            }
            notesToMerge = mergedNotes
        }

        return notesToMerge.first()
    }
}