package api

import data.Chatroom
import data.GenericMessage
import data.OllamaFinalResponse
import data.OllamaStreamResponse
import data.PromptWithHistory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import kotlin.collections.forEach

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

    init {
        val isOllamaRunning = isOllamaRunning()
        println("Is Ollama running: $isOllamaRunning")
    }

    fun generateStream(
        prompt: PromptWithHistory,
        model: String,
        onAddUserPrompt: (prompt: PromptWithHistory, model: String) -> Unit
    ){
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

        val ollamaSentence = formOllamaSentenceFromTokens(decodedList)

        addUserPrompt(prompt, model)

        val chatroom = decodedChatroomFile()

        val ollamaMessage = GenericMessage(
            role = "assistant",
            content = ollamaSentence
        )

        updateChatroom(chatroom, ollamaMessage, model)

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

    private fun formOllamaSentenceFromTokens(list: List<Any>): String{
        var sentence = ""
        val streamedResponses = list.take(list.size-1) as List<OllamaStreamResponse>
        streamedResponses.forEach { response ->
            sentence += response.message.content
        }

        println("Ollama reply to my prompt:\n\n$sentence")
        return sentence
    }

    fun getChatroomFile(): File {
        val testDir = File("testModule")
        val testFile = File(testDir, "test.json")
        return testFile
    }

    fun addUserPrompt(prompt: PromptWithHistory, model: String){
        val decoded = decodedChatroomFile()
        val messages = decoded.history.messages.toMutableList()
        messages.add(prompt.messages.last())

        val updatedHistory = decoded.history.copy(
            model = model,
            messages = messages.toList()
        )
        val updatedChatroom = decoded.copy(
            history = updatedHistory
        )
        writeToChatroomFile(json.encodeToString<Chatroom>(updatedChatroom))
    }

    fun updateChatroom(chatroom: Chatroom, ollamaMessage: GenericMessage, model: String){
        val decodedChatroom = decodedChatroomFile()
        val messages = decodedChatroom.history.messages.toMutableList()
        messages.add(ollamaMessage)

        val updatedHistory = chatroom.history.copy(
            model = model,
            messages = messages.toList()
        )
        val updatedChatroom = decodedChatroom.copy(history = updatedHistory)
        writeToChatroomFile(json.encodeToString<Chatroom>(updatedChatroom))
    }

    fun decodedChatroomFile() : Chatroom {
        val file = getChatroomFile()
        val fileContents = file.readText()
        val decodedChatroom = json.decodeFromString<Chatroom>(fileContents)
        return decodedChatroom
    }

    private fun writeToChatroomFile(text: String){
        val file = getChatroomFile()
        file.writeText(text)
    }

    fun isOllamaRunning(): Boolean {
        val request = Request.Builder()
            .url(ollamaUrl)
            .build()

        val response = client.newCall(request).execute()
        val stringResponse = response.body.string()

        return stringResponse == "Ollama is running"
    }
}