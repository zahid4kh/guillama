package api

import data.OllamaStreamResponse
import data.PromptWithHistory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class OllamaApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .callTimeout(600, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val ollamaUrl = "http://localhost:11434"
    private val baseApiUrl = "$ollamaUrl/api/chat"

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun generateStream(
        prompt: PromptWithHistory,
        onToken: (String) -> Unit,
        onGetStreamSummary: (String) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        try {
            val encodedPrompt = json.encodeToString(prompt)
            val request = Request.Builder()
                .url(baseApiUrl)
                .post(encodedPrompt.toRequestBody())
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}: ${response.message}")
            }

            response.body?.use { responseBody ->
                val reader = responseBody.charStream()

                try {
                    reader.forEachLine { line ->
                        if (line.isBlank()) return@forEachLine

                        try {
                            val streamResponse = json.decodeFromString<OllamaStreamResponse>(line)
                            if (!streamResponse.done) {
                                onToken(streamResponse.message.content)
                            } else {
                                onGetStreamSummary(line)
                            }
                        } catch (parseException: Exception) {
                            println("Error parsing stream response: $parseException")
                        }
                    }
                } catch (e: Exception) {
                    println("Error reading stream: $e")
                    onError(e)
                }
            } ?: run {
                onError(IOException("Empty response body"))
            }

        } catch (e: SocketTimeoutException) {
            println("Request timed out: $e")
            onError(e)
        } catch (e: IOException) {
            println("Network error: $e")
            onError(e)
        } catch (e: Exception) {
            println("Unexpected error: $e")
            onError(e)
        }
    }
}