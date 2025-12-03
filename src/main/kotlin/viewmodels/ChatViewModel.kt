package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Chatroom
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class ChatViewModel(
    private val mainViewModel: MainViewModel
): ViewModel() {
    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    private val chatsDir = File(".guillama/chats")

    init {
        viewModelScope.launch {
            delay(300)
            _chatUiState.update { it.copy(availableModels = mainViewModel.uiState.value.modelsLibrary) }
        }
    }

    fun createChatroom(){
        if(!chatsDir.exists()){
            chatsDir.mkdirs()
        }

//        val chatroom = Chatroom(
//            title =
//        )
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
        val chatRoomTitle: String = "",
        val selectedModel: String? = null,
        val userMessage: String = "",
        val modelMessage: String? = null
    )
}