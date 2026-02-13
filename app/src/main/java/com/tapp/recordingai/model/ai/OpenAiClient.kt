package com.tapp.recordingai.model.ai



import android.util.Log
import com.tapp.recordingai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

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
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun call(prompt: String): String =
        withContext(Dispatchers.IO) {

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

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException(
                        "OpenAI error ${response.code}: ${
                            response.body?.string()
                        }"
                    )
                }

                val rawJson = response.body?.string()
                    ?: throw IOException("Empty response")

                val root = JSONObject(rawJson)
                val output = root.getJSONArray("output")
                val content = output
                    .getJSONObject(0)
                    .getJSONArray("content")

                for (i in 0 until content.length()) {
                    val obj = content.getJSONObject(i)
                    if (obj.optString("type") == "output_text") {
                        return@withContext obj.optString("text")
                    }
                }

                throw IOException("No output_text found")
            }
        }
}
