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
import com.tapp.recordingai.utils.LanguageSet

class Recognizer(
    context: Context,
    private val onPartial: (String) -> Unit,
    private val onFinal: (String) -> Unit
) {

    private val appContext = context.applicationContext

    private val recognizer =
        SpeechRecognizer.createSpeechRecognizer(appContext)

    private val mainHandler = Handler(Looper.getMainLooper())

    private fun listenIntent(): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                LanguageSet.getSpeechLanguageTag(appContext)
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

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
        Log.d("speech", "startListening lang=${LanguageSet.getSpeechLanguageTag(appContext)}")
        recognizer.startListening(listenIntent())
    }

    fun stop() {
        if (!isListening) return
        isListening = false
        Log.d("speech", "stopListening")
        recognizer.cancel()
    }

    private fun scheduleListenAgainAfterResult() {
        if (!isListening) return
        mainHandler.post {
            if (!isListening) return@post
            try {
                recognizer.startListening(listenIntent())
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
                recognizer.startListening(listenIntent())
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
