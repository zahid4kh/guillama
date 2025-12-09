package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.OllamaApi
import data.Chatroom
import data.GenericMessage
import data.OllamaFinalResponse
import data.OllamaStreamResponse
import data.PromptWithHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.String

class ChatViewModel(
    private val mainViewModel: MainViewModel,
    private val api: OllamaApi
): ViewModel() {
    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    private val _lastMessageStats = MutableStateFlow(MessageStats())
    val lastMessageStats = _lastMessageStats.asStateFlow()

    private val homeDir = System.getProperty("user.home")
    val chatsDir = File("$homeDir/.guillama/chats")

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    init {
        viewModelScope.launch {
            delay(300)
            _chatUiState.update { it.copy(availableModels = mainViewModel.uiState.value.modelsLibrary) }
        }
    }

    fun createChatroom(){
        viewModelScope.launch(Dispatchers.IO) {
            if(!chatsDir.exists()){
                chatsDir.mkdirs()
            }

            val createdAt = System.currentTimeMillis()
            val formattedDate = convertMillisToFormattedDateTime(createdAt)
            val chatroom = Chatroom(
                title = formattedDate,
                modelInThisChatroom = null,
                id = createdAt
            )

            val jsonChatroom = json.encodeToString(chatroom)
            val chatroomFile = File(chatsDir, "${formattedDate}.json")
            chatroomFile.writeText(jsonChatroom)

            loadChatroom(chatroom, chatroomFile)
        }
    }

    fun loadChatroom(chatroom: Chatroom, file: File? = null){
        val chatroomFile = file ?: run {
            val formattedDate = convertMillisToFormattedDateTime(chatroom.id)
            File(chatsDir, "${formattedDate}.json")
        }

        _chatUiState.update {
            it.copy(
                loadedChatroom = chatroom,
                loadedChatroomFile = chatroomFile,
                chatRoomTitle = chatroom.title,
                selectedModel = chatroom.modelInThisChatroom,
                isStreaming = false
            )
        }
        loadMessages()
    }

    fun loadMessages(){
        val currentChatroom = getDecodedChatroomFile()
        val messages = currentChatroom.history.messages
        _chatUiState.update {
            it.copy(
                messages = messages.reversed(),
                loadedChatroom = currentChatroom
            )
        }
    }

    private fun getChatroomFile(): File?{
        return _chatUiState.value.loadedChatroomFile
    }

    private fun getDecodedChatroomFile() : Chatroom{
        val file = getChatroomFile()
        if (file == null || !file.exists()) {
            return _chatUiState.value.loadedChatroom ?: Chatroom()
        }
        val jsonFileContent = file.readText()
        return json.decodeFromString<Chatroom>(jsonFileContent)
    }

    fun updateSaveChatroom(){
        viewModelScope.launch(Dispatchers.IO) {
            val chatroom = _chatUiState.value.loadedChatroom
            val file = _chatUiState.value.loadedChatroomFile

            if (chatroom != null && file != null) {
                val updated = chatroom.copy(
                    title = _chatUiState.value.chatRoomTitle,
                    modelInThisChatroom = _chatUiState.value.selectedModel
                )
                file.writeText(json.encodeToString(updated))
                showMessage("Chatroom updated!")

                viewModelScope.launch(Dispatchers.Main) {
                    loadMessages()
                }
            }
        }
    }

    private fun addUserMessage(message: GenericMessage){
        val decoded = getDecodedChatroomFile()
        val messages = decoded.history.messages.toMutableList()
        messages.add(message)

        val updatedHistory = decoded.history.copy(
            messages = messages.toList()
        )
        val updatedChatroom = decoded.copy(history = updatedHistory)
        writeToChatroomFile(json.encodeToString<Chatroom>(updatedChatroom))
    }

    private fun addAssistantMessage(message: GenericMessage){
        val decoded = getDecodedChatroomFile()
        val messages = decoded.history.messages.toMutableList()
        messages.add(message)

        val updatedHistory = decoded.history.copy(
            model = _chatUiState.value.selectedModel ?: "",
            messages = messages.toList()
        )
        val updatedChatroom = decoded.copy(history = updatedHistory)
        writeToChatroomFile(json.encodeToString<Chatroom>(updatedChatroom))
    }

    private fun updateLastAssistantMessage(content: String){
        val decoded = getDecodedChatroomFile()
        val messages = decoded.history.messages.toMutableList()

        if(messages.isNotEmpty() && messages.last().role == "assistant"){
            messages[messages.size - 1] = messages.last().copy(content = content)

            val updatedHistory = decoded.history.copy(
                model = _chatUiState.value.selectedModel ?: "",
                messages = messages.toList()
            )
            val updatedChatroom = decoded.copy(history = updatedHistory)
            writeToChatroomFile(json.encodeToString<Chatroom>(updatedChatroom))
        }
    }

    private fun writeToChatroomFile(text: String){
        val file = getChatroomFile()
        file?.writeText(text)
    }

    fun sendMessage(){
        val userMessageText = _chatUiState.value.userMessage.trim()
        if(userMessageText.isEmpty()) return

        _chatUiState.update { it.copy(userMessage = "") }

        viewModelScope.launch(Dispatchers.IO) {
            val userMessage = GenericMessage(
                role = "user",
                content = userMessageText
            )

            addUserMessage(userMessage)

            viewModelScope.launch(Dispatchers.Main) {
                loadMessages()
            }

            val chatroomHistory = getDecodedChatroomFile().history
            val allMessages = chatroomHistory.messages

            val prompt = PromptWithHistory(
                model = _chatUiState.value.selectedModel ?: "",
                messages = allMessages
            )

            val assistantMessage = GenericMessage(role = "assistant", content = "")
            addAssistantMessage(assistantMessage)

            viewModelScope.launch(Dispatchers.Main) {
                _chatUiState.update { it.copy(isStreaming = true) }
                loadMessages()
            }

            var fullResponse = ""
            api.generateStream(
                prompt = prompt,
                onToken = {token ->
                    fullResponse += token

                    updateLastAssistantMessage(fullResponse)

                    viewModelScope.launch(Dispatchers.Main) {
                        loadMessages()
                    }
                },
                onGetStreamSummary = { line ->
                    val decodedLastLine = json.decodeFromString<OllamaFinalResponse>(line)
                    val messageHash = fullResponse.hashCode().toString()
                    println("$decodedLastLine")

                    val stats = MessageStats(
                        messageHash = messageHash,
                        createdAt = isoTimestampToLocalFormat(decodedLastLine.createdAt, 1),
                        totalDuration = formatDuration(decodedLastLine.totalDuration),
                        loadDuration = formatDuration(decodedLastLine.loadDuration),
                        promptEvalCount = decodedLastLine.promptEvalCount.toString(),
                        promptEvalDuration = formatDuration(decodedLastLine.promptEvalDuration),
                        evalCount = decodedLastLine.evalCount.toString(),
                        evalDuration = formatDuration(decodedLastLine.evalDuration),
                        generaationSpeed = calculateTokensPerSecond(decodedLastLine.evalCount, decodedLastLine.evalDuration)
                    )
                    println("STATS:\n$stats")

                    _chatUiState.update { state ->
                        state.copy(
                            currentSessionStats = state.currentSessionStats + (messageHash to stats)
                        )
                    }
                }
            )

            viewModelScope.launch(Dispatchers.Main) {
                _chatUiState.update { it.copy(isStreaming = false) }
            }
        }
    }

    fun getStatsForMessage(message: GenericMessage): MessageStats? {
        val messageHash = message.content.hashCode().toString()
        return _chatUiState.value.currentSessionStats[messageHash]
    }

    private fun calculateTokensPerSecond(tokenCount: Int, durationNanos: Long): String {
        if (durationNanos == 0L) return "0.0"

        val tokensPerSecond = (tokenCount.toDouble() * 1_000_000_000.0) / durationNanos.toDouble()
        return String.format("%.1f", tokensPerSecond)
    }

    private fun isoTimestampToLocalFormat(isoTimestamp: String, zoneOffsetHours: Int = 0): String {
        val utc = OffsetDateTime.parse(isoTimestamp.replace("Z", "+00:00"))
        val local = utc.withOffsetSameInstant(ZoneOffset.ofHours(zoneOffsetHours))
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss")
        return local.format(formatter)
    }

    fun nanosToSeconds(nanos: Long): Double = nanos / 1_000_000_000.0

    fun formatDuration(nanos: Long): String {
        val seconds = nanos / 1_000_000_000.0
        return when {
            seconds < 1 -> String.format("%.1f ms", seconds * 1000)
            seconds < 60 -> String.format("%.3f s", seconds)
            else -> String.format("%.1f s", seconds)
        }
    }

    private suspend fun showMessage(message: String){
        _chatUiState.update { it.copy(message = message) }
        delay(1000)
        _chatUiState.update { it.copy(message = "") }
    }

    fun convertMillisToFormattedDateTime(milliseconds: Long): String {
        val instant = Instant.ofEpochMilli(milliseconds)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss")
        return dateTime.format(formatter)
    }

    fun onSelectModel(modelName: String){
        _chatUiState.update { it.copy(selectedModel = modelName) }
        closeDropdown()
    }

    fun expandDropdown(){
        _chatUiState.update { it.copy(showModelSelectorDropdown = true) }
    }

    fun closeDropdown(){
        _chatUiState.update { it.copy(showModelSelectorDropdown = false) }
    }

    fun onUserMessageTyped(text: String){
        _chatUiState.update { it.copy(userMessage = text) }
    }

    fun handleEditSaveTitle(){
        _chatUiState.update {
            it.copy(
                isEditingTitle = !it.isEditingTitle,
                chatRoomTitle = _chatUiState.value.chatRoomTitle
            )
        }
    }

    fun saveNewTitle(newTitle: String){
        _chatUiState.update { it.copy(chatRoomTitle = newTitle) }
    }

    fun toggleMessageStats(){
        _chatUiState.update { it.copy(showMessageStats = !it.showMessageStats) }
    }

    data class ChatUiState(
        val availableModels: List<String> = emptyList(),
        val showModelSelectorDropdown: Boolean = false,
        val isEditingTitle: Boolean = false,
        val chatRoomTitle: String = "nothing",
        val message: String = "",
        val selectedModel: String? = null,
        val userMessage: String = "",
        val modelMessage: String? = null,
        val messages: List<GenericMessage> = emptyList(),
        val loadedChatroom: Chatroom? = null,
        val loadedChatroomFile: File? = null,
        val isStreaming: Boolean = false,
        val showMessageStats: Boolean = false,
        val currentSessionStats: Map<String, MessageStats> = emptyMap()
    )

    data class MessageStats(
        val messageHash: String = "",
        val createdAt: String = "",
        val totalDuration: String = "",
        val loadDuration: String = "",
        val promptEvalCount: String = "",
        val promptEvalDuration: String = "",
        val evalCount: String = "",
        val evalDuration: String = "",
        val generaationSpeed: String = ""
    )
}