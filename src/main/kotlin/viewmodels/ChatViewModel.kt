package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.OllamaApi
import data.Chatroom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ChatViewModel(
    private val mainViewModel: MainViewModel,
    private val api: OllamaApi
): ViewModel() {
    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

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
                selectedModel = chatroom.modelInThisChatroom
            )
        }
        println("CHATVIEWMODEL: Loaded chatroom: ${_chatUiState.value.loadedChatroom}")
    }

    private fun getChatroomFile(): File?{
        val file = _chatUiState.value.loadedChatroomFile
        return file
    }

    private fun getDecodedChatroomFile() : Chatroom{
        val jsonFileContent = getChatroomFile()?.readText()?:""
        val decoded = json.decodeFromString<Chatroom>(jsonFileContent)
        return decoded
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
                println("Updated chatroom: $updated")
                showMessage("Chatroom updated!")
            }
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
        println("Selected model: ${_chatUiState.value.selectedModel}")
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

    data class ChatUiState(
        val availableModels: List<String> = emptyList(),
        val showModelSelectorDropdown: Boolean = false,
        val isEditingTitle: Boolean = false,
        val chatRoomTitle: String = "nothing",
        val message: String = "",
        val selectedModel: String? = null,
        val userMessage: String = "",
        val modelMessage: String? = null,
        val loadedChatroom: Chatroom? = null,
        val loadedChatroomFile: File? = null
    )
}