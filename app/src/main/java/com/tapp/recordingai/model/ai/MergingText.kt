package com.tapp.recordingai.model.ai

import kotlinx.coroutines.withTimeout

/**
 * Иерархическое слияние: пока больше одного фрагмента, батчи объединяются через AI.
 * Размер батча подбирается по **символам**, чтобы промпт не раздувался (длинная лекция → много уровней «дерева»).
 */
object MergingText {

    /** Макс. суммарная длина текстов в одном вызове merge (без обёртки промпта). */
    private const val MAX_MERGE_BATCH_CHARS = 12_000

    /** Максимум частей в батче, если все укладываются в [MAX_MERGE_BATCH_CHARS]. */
    private const val MAX_BATCH_ITEMS = 5

    private const val MERGE_TIMEOUT_MS = 120_000L

    suspend fun reduce(
        chunks: List<String>,
        client: OpenAiClient
    ): String {
        if (chunks.isEmpty()) return ""
        var level = chunks.map { it.trim() }.filter { it.isNotEmpty() }
        if (level.isEmpty()) return ""

        while (level.size > 1) {
            val nextLevel = mutableListOf<String>()
            var i = 0
            while (i < level.size) {
                val batch = takeMergeBatch(level, i)
                i += batch.size
                if (batch.size == 1) {
                    nextLevel += batch.single()
                } else {
                    val merged = mergeBatch(batch, client)
                    nextLevel += merged
                }
            }
            level = nextLevel
        }
        return level.first()
    }

    /**
     * Жадно собирает подряд идущие куски: пока суммарная длина ≤ [MAX_MERGE_BATCH_CHARS].
     * Если один кусок уже больше лимита — уносим его отдельно; если один кусок + следующий
     * не влезают, но в батче ещё один элемент — всё равно добавляем второй (иныче прогресс остановится).
     */
    private fun takeMergeBatch(pieces: List<String>, from: Int): List<String> {
        if (from >= pieces.size) return emptyList()
        val first = pieces[from]
        if (from == pieces.lastIndex) return listOf(first)

        val batch = mutableListOf(first)
        var total = first.length
        var idx = from + 1

        while (idx < pieces.size && batch.size < MAX_BATCH_ITEMS) {
            val next = pieces[idx]
            if (total + next.length <= MAX_MERGE_BATCH_CHARS) {
                batch.add(next)
                total += next.length
                idx++
            } else {
                if (batch.size > 1) break
                batch.add(next)
                idx++
                break
            }
        }
        return batch
    }

    private suspend fun mergeBatch(batch: List<String>, client: OpenAiClient): String {
        if (batch.isEmpty()) return ""
        if (batch.size == 1) return batch.single()
        return try {
            withTimeout(MERGE_TIMEOUT_MS) {
                client.call(PromptFactory.mergeChunks(batch))
            }
        } catch (e: Exception) {
            batch.joinToString("\n\n---\n\n")
        }
    }
}
