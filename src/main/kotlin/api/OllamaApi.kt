package api

import okhttp3.OkHttpClient
import okhttp3.Request

class OllamaApi {
    val client = OkHttpClient()

    init {
        val isOllamaRunning = isOllamaRunning()
        println("Is Ollama running: $isOllamaRunning")
    }

    fun generateStream(model: String, prompt: String){

    }

    fun isOllamaRunning(): Boolean {
        val ollamaUrl = "http://localhost:11434"
        val request = Request.Builder()
            .url(ollamaUrl)
            .build()

        val response = client.newCall(request).execute()
        val stringResponse = response.body.string()

        return stringResponse == "Ollama is running"
    }
}