package com.tapp.recordingai.model.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class Recognizer(
    context: Context,
    private val onFinal: (String) -> Unit
) {

    private val recognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    init {
        recognizer.setRecognitionListener(object : RecognitionListener {

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()

                if (!text.isNullOrBlank()) {
                    onFinal(text)
                }

                restart()
            }

            override fun onError(error: Int) {
                restart()
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
        recognizer.stopListening()
    }


    private fun restart() {
        if (!isListening) return
        recognizer.cancel()
        recognizer.startListening(intent)
    }

    fun release() {
        recognizer.cancel()
        recognizer.destroy()
    }

}