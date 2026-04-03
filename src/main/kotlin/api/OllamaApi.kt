package api

import data.ModelsResponse
import data.OllamaStreamResponse
import data.PromptWithHistory
import data.PullProgress
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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

    private val longOperationClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .callTimeout(0, TimeUnit.SECONDS)
        .build()

    private val ollamaUrl = "http://localhost:11434"
    private val baseApiUrl = "$ollamaUrl/api/chat"
    private val tagsApiUrl = "$ollamaUrl/api/tags"
    private val deleteApiUrl = "$ollamaUrl/api/delete"
    private val pullApiUrl = "$ollamaUrl/api/pull"

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

    fun deleteModel(modelName: String): Boolean {
        return try {
            val body = buildJsonObject { put("model", modelName) }.toString()
            val request = Request.Builder()
                .url(deleteApiUrl)
                .delete(body.toRequestBody())
                .build()
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            println("Error deleting model \"$modelName\": $e")
            false
        }
    }

    fun pullModel(
        modelName: String,
        onProgress: (PullProgress) -> Unit,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val body = buildJsonObject { put("model", modelName) }.toString()
            val request = Request.Builder()
                .url(pullApiUrl)
                .post(body.toRequestBody())
                .build()

            val response = longOperationClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}: ${response.message}")
            }

            var succeeded = false
            response.body?.use { responseBody ->
                responseBody.charStream().forEachLine { line ->
                    if (line.isBlank()) return@forEachLine
                    try {
                        val progress = json.decodeFromString<PullProgress>(line)
                        onProgress(progress)
                        if (progress.status == "success") succeeded = true
                    } catch (e: Exception) {
                        println("Error parsing pull response: $e")
                    }
                }
            } ?: throw IOException("Empty response body")

            if (succeeded) onComplete()
            else onError(IOException("Pull ended without success"))

        } catch (e: SocketTimeoutException) {
            onError(e)
        } catch (e: IOException) {
            onError(e)
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun getAvailableModels(): ModelsResponse? {
        return try {
            val request = Request.Builder()
                .url(tagsApiUrl)
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                println("Failed to fetch models: HTTP ${response.code}: ${response.message}")
                return null
            }

            response.body?.use { responseBody ->
                val responseString = responseBody.string()
                json.decodeFromString<ModelsResponse>(responseString)
            }
        } catch (e: Exception) {
            println("Error fetching models: $e")
            null
        }
    }
}