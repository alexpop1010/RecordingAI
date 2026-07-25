package com.tapp.recordingai.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.tapp.recordingai.R
import java.io.File
import java.io.FileOutputStream

object NoteShareHelper {

    fun sharePlainText(context: Context, subject: String, text: String) {
        if (text.isBlank()) return
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(send, subject))
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        if (text.isBlank()) return
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, R.string.note_copied, Toast.LENGTH_SHORT).show()
    }

    fun exportTxtAndShare(context: Context, title: String, body: String) {
        try {
            val file = writeTxt(context, title, body)
            shareFile(context, file, "text/plain")
        } catch (_: Exception) {
            Toast.makeText(context, R.string.note_export_failed, Toast.LENGTH_SHORT).show()
        }
    }

    fun exportPdfAndShare(context: Context, title: String, body: String) {
        try {
            val file = writePdf(context, title, body)
            shareFile(context, file, "application/pdf")
        } catch (_: Exception) {
            Toast.makeText(context, R.string.note_export_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(context: Context, file: File, mime: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val send = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_SUBJECT, file.name.substringBeforeLast('.'))
        }
        context.startActivity(Intent.createChooser(send, file.name))
    }

    private fun writeTxt(context: Context, title: String, body: String): File {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val name = "${sanitizeFileName(title)}_${System.currentTimeMillis()}.txt"
        val file = File(dir, name)
        file.writeText(buildString {
            if (title.isNotBlank()) {
                append(title)
                append("\n\n")
            }
            append(body)
        }, Charsets.UTF_8)
        return file
    }

    private fun writePdf(context: Context, title: String, body: String): File {
        val full = buildString {
            if (title.isNotBlank()) {
                append(title)
                append("\n\n")
            }
            append(body)
        }.ifBlank { " " }

        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 12 * context.resources.displayMetrics.scaledDensity
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val pageW = 595
        val pageH = 842
        val margin = 48f
        val contentW = (pageW - 2 * margin).toInt().coerceAtLeast(100)

        val layout = StaticLayout.Builder.obtain(full, 0, full.length, paint, contentW)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .build()

        val doc = PdfDocument()
        var yOffset = 0f
        val sliceH = pageH - 2 * margin
        var pageNum = 1
        while (yOffset < layout.height + 0.5f) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create()
            val page = doc.startPage(pageInfo)
            val canvas = page.canvas
            canvas.save()
            canvas.translate(margin, margin - yOffset)
            layout.draw(canvas)
            canvas.restore()
            doc.finishPage(page)
            yOffset += sliceH
            pageNum++
        }

        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "${sanitizeFileName(title)}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file
    }

    private fun sanitizeFileName(title: String): String =
        title.replace(Regex("[^\\w\\s\\-А-Яа-яЁё]"), "_")
            .trim()
            .take(48)
            .ifBlank { "note" }
}
