package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val mainViewModel: MainViewModel
): ViewModel() {
    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    private val homeDir = System.getProperty("user.home")
    val chatsDir = File("$homeDir/.guillama/chats")

    private val json = Json {
        prettyPrint = true
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
                selectedModel = null,
                createdAt = createdAt
            )

            val jsonChatroom = json.encodeToString(chatroom)
            val chatroomFile = File(chatsDir, "${formattedDate}.json")
            chatroomFile.writeText(jsonChatroom)

            loadChatroom(chatroom)
        }
    }

    fun loadChatroom(chatroom: Chatroom){
        _chatUiState.update {
            it.copy(
                loadedChatroom = chatroom,
                chatRoomTitle = chatroom.title,
            )
        }
        println("CHATVIEWMODEL:  Loaded chatroom: ${_chatUiState.value.loadedChatroom}")
    }

    fun updateSaveChatroom(){
        viewModelScope.launch(Dispatchers.IO) {
            _chatUiState.value.loadedChatroom?.let { chatroom ->
                val chatroomTitle = _chatUiState.value.chatRoomTitle
                val formattedDate = convertMillisToFormattedDateTime(chatroom.createdAt)
                val chatroomFileName = File(chatsDir, "${formattedDate}.json")

                val updated = chatroom.copy(
                    title = chatroomTitle,
                    selectedModel = _chatUiState.value.selectedModel
                )
                chatroomFileName.writeText(json.encodeToString(updated))
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
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm")
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
    )
}