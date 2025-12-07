package api

import data.OllamaFinalResponse
import data.OllamaStreamResponse
import data.PromptWithHistory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class OllamaApi(

) {
    private val client = OkHttpClient()
    private val ollamaUrl = "http://localhost:11434"
    private val baseApiUrl = "$ollamaUrl/api/chat"

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun generateStream(
        prompt: PromptWithHistory
    ) : MutableList<Any> {
        val encodedPrompt = json.encodeToString(prompt)
        val request = createRequest(encodedPrompt)

        val response = getResponse(request)

        val streamList = mutableListOf<String>()
        val decodedList = mutableListOf<Any>()

        val reader = response.body.charStream()
        reader.forEachLine { line ->
            if(line.isBlank()) return@forEachLine
            streamList.add(line)
        }

        streamList.forEach { js ->
            decodedList.add(
                json.decodeFromString<OllamaStreamResponse>(js)
            )
            if(js == streamList.last()){
                decodedList.add(
                    json.decodeFromString<OllamaFinalResponse>(js)
                )
            }
        }
        return decodedList

    }

    private fun createRequest(bodyPrompt: String) : Request{
        val request = Request.Builder()
            .url(baseApiUrl)
            .post(bodyPrompt.toRequestBody())
            .build()

        println("REQUEST BODY:\n\n${request.toCurl()}")

        return request
    }

    private fun getResponse(request: Request) : Response {
        val response = client.newCall(request).execute()
        if(!response.isSuccessful) throw IOException("Unexpected code $response")
        return response
    }
}