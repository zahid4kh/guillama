package api

import data.OllamaStreamResponse
import data.PromptWithHistory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class OllamaApi {
    private val client = OkHttpClient()
    private val ollamaUrl = "http://localhost:11434"
    private val baseApiUrl = "$ollamaUrl/api/chat"

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun generateStream(
        prompt: PromptWithHistory,
        onToken: (String) -> Unit
    ) {
        val encodedPrompt = json.encodeToString(prompt)
        val request = Request.Builder()
            .url(baseApiUrl)
            .post(encodedPrompt.toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val reader = response.body.charStream()
        reader.forEachLine { line ->
            if (line.isBlank()) return@forEachLine

            try {
                val streamResponse = json.decodeFromString<OllamaStreamResponse>(line)
                if (!streamResponse.done) {
                    onToken(streamResponse.message.content)
                }
            } catch (e: Exception) {
                println("Error parsing stream response: $e")
            }
        }
    }
}