package com.tapp.recordingai.model.ai
import kotlinx.coroutines.withTimeout

object MergingText {
    private const val GROUP_SIZE = 5
    suspend fun reduce(
        chunks: List<String>,
        client: OpenAiClient
    ): String {
        if (chunks.isEmpty()) return ""
        var notesToMerge = chunks
        while (notesToMerge.size > 1) {
            val mergedNotes = mutableListOf<String>()
            val groups = notesToMerge.chunked(GROUP_SIZE)
            for ((index, group) in groups.withIndex()) {
                val merged = try {
                    withTimeout(30_000) {
                        client.call(
                            PromptFactory.mergeChunks(group)
                        )
                    }
                } catch (e: Exception) {
                    group.joinToString("\n\n")
                }

                mergedNotes += merged
            }

            notesToMerge = mergedNotes
        }
        return notesToMerge.first()
    }
}
