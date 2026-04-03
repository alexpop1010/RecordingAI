package com.tapp.recordingai.model.ai

object PromptFactory {

    fun structureChunk(
        text: String,
        partIndex: Int = 1,
        totalParts: Int = 1
    ): String {
        val partHint = if (totalParts > 1) {
            """
            Это фрагмент ${partIndex} из $totalParts одной лекции/диктовки.
            В следующих фрагментах продолжение; не пытайся «завершить» тему, если мысль обрывается на границе фрагмента.
            Сохраняй термины и имена как в тексте.

            """.trimIndent()
        } else {
            ""
        }

        return """
            Ты — помощник, который превращает голосовые конспекты
            в структурированные заметки.

            Правила:
            не добавляй новых фактов
            убери повторы и мусорные слова
            сохрани смысл
            оформи результат в Markdown
            используй заголовки и списки
            $partHint
            Текст:
            <<<
            $text
            >>>
        """.trimIndent()
    }

    fun mergeChunks(chunks: List<String>): String {
        val label = if (chunks.size > 1) {
            "Ниже ${chunks.size} частей одного конспекта — объедини в один документ без потери разделов."
        } else {
            ""
        }
        return """
            Ты — помощник, который объединяет несколько
            структурированных заметок в один цельный конспект.

            Правила:
            не добавляй новых фактов
            убери повторы на стыках частей
            сохрани Markdown-структуру (заголовки, списки)
            выстрой логичный порядок — части даны последовательно
            $label

            Заметки:
            <<<
            ${chunks.mapIndexed { i, c -> "### Часть ${i + 1}\n$c" }.joinToString("\n\n")}
            >>>
        """.trimIndent()
    }
}
