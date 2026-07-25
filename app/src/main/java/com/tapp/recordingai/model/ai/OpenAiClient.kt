package com.tapp.recordingai.model.ai

import android.util.Log
import com.tapp.recordingai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OpenAiClient {

    companion object {
        private const val URL = "https://api.openai.com/v1/responses"
    }

    private val logger = HttpLoggingInterceptor {
        Log.d("OpenAI_HTTP", it)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(180, TimeUnit.SECONDS)
        .build()

    suspend fun call(prompt: String): String =
        suspendCancellableCoroutine { cont ->

            val bodyJson = JSONObject().apply {
                put("model", "gpt-4.1-mini")
                put("input", prompt)
            }

            val request = Request.Builder()
                .url(URL)
                .addHeader(
                    "Authorization",
                    "Bearer ${BuildConfig.OPENAI_API_KEY}"
                )
                .addHeader("Content-Type", "application/json")
                .post(
                    bodyJson.toString()
                        .toRequestBody("application/json".toMediaType())
                )
                .build()

            val call = client.newCall(request)


            cont.invokeOnCancellation {
                call.cancel()
            }

            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    if (cont.isCancelled) return
                    cont.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {

                        if (!response.isSuccessful) {
                            cont.resumeWithException(
                                IOException("HTTP ${response.code}")
                            )
                            return
                        }

                        val rawJson = response.body?.string()
                            ?: run {
                                cont.resumeWithException(
                                    IOException("Empty body")
                                )
                                return
                            }

                        val root = JSONObject(rawJson)
                        val output = root.getJSONArray("output")
                        val content = output
                            .getJSONObject(0)
                            .getJSONArray("content")

                        for (i in 0 until content.length()) {
                            val obj = content.getJSONObject(i)
                            if (obj.optString("type") == "output_text") {
                                cont.resume(obj.optString("text"))
                                return
                            }
                        }

                        cont.resumeWithException(
                            IOException("No output_text")
                        )
                    }
                }
            })
        }
}
