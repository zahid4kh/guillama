package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Chat
import data.Chatroom
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File


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
            val chatroom = Chatroom(
                title = "",
                selectedModel = null,
                createdAt = createdAt
            )

            val jsonChatroom = json.encodeToString(chatroom)
            val chatroomFile = File(chatsDir, "${_chatUiState.value.chatRoomTitle}_${createdAt}.json")
            chatroomFile.writeText(jsonChatroom)

            _chatUiState.update {
                it.copy(loadedChatroom = chatroom)
            }
            println("Currently loaded chatroom: ${_chatUiState.value.loadedChatroom}")
        }
    }

    fun updateSaveChatroom(){
        _chatUiState.value.loadedChatroom?.let { chatroom ->
            val chatroomTitle = _chatUiState.value.chatRoomTitle
            val chatroomFileName = File(chatsDir, "${_chatUiState.value.chatRoomTitle}_${chatroom.createdAt}.json")
        }
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
        val chatRoomTitle: String = "Untitled",
        val selectedModel: String? = null,
        val userMessage: String = "",
        val modelMessage: String? = null,
        val loadedChatroom: Chatroom? = null,
    )
}