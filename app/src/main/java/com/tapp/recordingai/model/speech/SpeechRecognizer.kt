package com.tapp.recordingai.model.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class Recognizer(
    context: Context,
    private val onPartial: (String) -> Unit,
    private val onFinal: (String) -> Unit
) {

    private val recognizer =
        SpeechRecognizer.createSpeechRecognizer(context.applicationContext)

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        recognizer.setRecognitionListener(object : RecognitionListener {

            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    onPartial(text)
                }
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()

                if (!text.isNullOrBlank()) {
                    onFinal(text)
                }

                scheduleListenAgainAfterResult()
            }

            override fun onError(error: Int) {
                // После cancel() (стоп записи) и при перезапуске сессии часто приходит ERROR_CLIENT — не перезапускать.
                if (error == SpeechRecognizer.ERROR_CLIENT) return
                if (!isListening) return

                when (error) {
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        mainHandler.postDelayed({ listenAgainAfterError() }, 200)
                    else -> listenAgainAfterError()
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private var isListening = false

    fun start() {
        if (isListening) return
        isListening = true
        Log.d("speech", "startListening")
        recognizer.startListening(intent)
    }

    fun stop() {
        if (!isListening) return
        isListening = false
        Log.d("speech", "stopListening")
        recognizer.cancel()
    }

    /**
     * После [onResults] сессия уже завершена — не вызывать [SpeechRecognizer.cancel],
     * иначе прилетит ERROR_CLIENT и сломает следующий цикл распознавания.
     */
    private fun scheduleListenAgainAfterResult() {
        if (!isListening) return
        mainHandler.post {
            if (!isListening) return@post
            try {
                recognizer.startListening(intent)
            } catch (e: Exception) {
                Log.w("speech", "listen again after result failed", e)
            }
        }
    }

    private fun listenAgainAfterError() {
        if (!isListening) return
        recognizer.cancel()
        mainHandler.post {
            if (!isListening) return@post
            try {
                recognizer.startListening(intent)
            } catch (e: Exception) {
                Log.w("speech", "listen again after error failed", e)
            }
        }
    }

    fun release() {
        isListening = false
        mainHandler.removeCallbacksAndMessages(null)
        recognizer.cancel()
        recognizer.destroy()
    }
}
