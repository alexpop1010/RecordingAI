package com.tapp.recordingai.model.ai

/**
 * Делит длинные транскрипты (лекции, диктовка) на куски для API.
 * Старается резать по абзацам, строкам и концам предложений, а не посередине слова.
 */
object TextChunker {

    /** Целевой максимум символов в одном чанке (тело без учёта системного промпта). */
    const val MAX_CHARS = 5_500

    /** Не брать границу слишком близко к началу чанка — иначе получатся крошечные фрагменты. */
    private const val MIN_TAIL_RATIO = 0.42

    /** Небольшое перекрытие между соседними чанками, чтобы не терять контекст на стыке. */
    private const val OVERLAP_CHARS = 200

    fun split(text: String): List<String> {
        val normalized = text.replace("\r\n", "\n").trim()
        if (normalized.isEmpty()) return emptyList()

        val chunks = mutableListOf<String>()
        var index = 0

        while (index < normalized.length) {
            val hardEnd = (index + MAX_CHARS).coerceAtMost(normalized.length)
            if (hardEnd >= normalized.length) {
                chunks += normalized.substring(index).trim()
                break
            }

            val window = normalized.substring(index, hardEnd)
            val minSplit = ((window.length) * MIN_TAIL_RATIO).toInt().coerceAtLeast(1)
            val relativeBreak = findBestBreakEnd(window, minSplit)
            val endExcl = index + relativeBreak
            val piece = normalized.substring(index, endExcl).trim()
            if (piece.isNotEmpty()) {
                chunks += piece
            }

            val nextStart = (endExcl - OVERLAP_CHARS).coerceAtLeast(index + 1)
            index = nextStart.coerceAtMost(normalized.length)
            if (index >= normalized.length) break
        }

        return chunks.filter { it.isNotBlank() }
    }

    /**
     * Ищет конец подстроки [0, [relativeEnd]) относительно начала окна.
     * @param window кусок текста длины ≤ MAX_CHARS
     * @param minEnd минимальная длина первого чанка (чтобы не резать слишком рано)
     */
    private fun findBestBreakEnd(window: String, minEnd: Int): Int {
        val ceiling = window.length
        val from = minEnd.coerceAtMost(ceiling)

        val breakAfterParagraph = window.lastIndexOf("\n\n", ceiling - 1).let { i ->
            if (i >= from - 1) i + 2 else -1
        }
        if (breakAfterParagraph > 0) return breakAfterParagraph.coerceAtMost(ceiling)

        val breakAfterLine = window.lastIndexOf('\n', ceiling - 1).let { i ->
            if (i >= from - 1) i + 1 else -1
        }
        if (breakAfterLine > 0) return breakAfterLine.coerceAtMost(ceiling)

        val sentenceEnds = listOf(". ", "? ", "! ", "… ", ".\n", "?\n", "!\n")
        var best = -1
        for (sep in sentenceEnds) {
            var searchFrom = ceiling - sep.length
            while (searchFrom >= from - 1) {
                val idx = window.lastIndexOf(sep, searchFrom)
                if (idx < from - 1) break
                val endPos = idx + sep.length
                if (endPos > best) best = endPos
                searchFrom = idx - 1
            }
        }
        if (best > 0) return best.coerceAtMost(ceiling)

        val space = window.lastIndexOf(' ', ceiling - 1)
        if (space >= from - 1) return (space + 1).coerceAtMost(ceiling)

        return ceiling
    }
}
