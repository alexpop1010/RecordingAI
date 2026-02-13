package com.tapp.recordingai.model.ai



object TextChunker {

    const val MAX_CHARS = 6_000

    fun split(text: String): List<String> {
        val chunks = mutableListOf<String>()
        var start = 0

        while (start < text.length) {
            val end = (start + MAX_CHARS).coerceAtMost(text.length)
            chunks += text.substring(start, end)
            start = end
        }
        return chunks
    }
}
