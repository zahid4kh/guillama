package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Chatroom
import data.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File

class MainViewModel(
    private val database: Database,
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val modelLibraryPath = File("/usr/share/ollama/.ollama/models/manifests/registry.ollama.ai/library")

    private val homeDir = System.getProperty("user.home")
    val chatsDir = File("$homeDir/.guillama/chats")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = database.getSettings()
            _uiState.value = _uiState.value.copy(
                darkMode = settings.darkMode,
            )

            listChatRooms()
            checkModels()
        }
    }

    private fun checkModels(){
        if(!modelLibraryPath.exists()){
            println("Default path for ollama models '$modelLibraryPath' was not found")
            return
        }else{
            val numOfModels = modelLibraryPath.listFiles().size
            println("Found $numOfModels models")
            modelLibraryPath.listFiles().forEach { file ->
                if(file.isDirectory){
                    val modelParameterNum = file.listFiles()
                    modelParameterNum.forEach { params ->
                        val fullModelName = "${file.name}:${params.nameWithoutExtension}"
                        _uiState.update {
                            it.copy(
                                modelsLibrary = it.modelsLibrary + fullModelName
                            )
                        }
                        println("Model: $fullModelName")
                    }
                }
            }
        }
    }

    fun showModelListDialog(){
        _uiState.update { it.copy(modelListDialogShown = true) }
    }

    fun closeModelListDialog(){
        _uiState.update { it.copy(modelListDialogShown = false) }
    }

    fun showSideDrawer(){
        _uiState.update { it.copy(drawerShown = true) }
    }

    fun closeSideDrawer(){
        _uiState.update { it.copy(drawerShown = false) }
    }

    fun listChatRooms(){
        viewModelScope.launch {
            if(chatsDir.exists()){
                val files = chatsDir.listFiles().toList()
                println("Found files in $chatsDir: $files")
                _uiState.update {
                    it.copy(listOfChatrooms = files)
                }
            }
        }
    }

    fun selectChatroom(chatroom: File){
        val decoded = json.decodeFromString<Chatroom>(chatroom.readText())
        _uiState.update { it.copy(selectedChatroomTimestamp = decoded.createdAt) }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.darkMode
        _uiState.value = _uiState.value.copy(darkMode = newDarkMode)

        viewModelScope.launch {
            val settings = database.getSettings()
            database.saveSettings(settings.copy(darkMode = newDarkMode))
        }
    }

    data class UiState(
        val darkMode: Boolean = false,
        val modelsLibrary: List<String> = mutableListOf(),
        val modelListDialogShown: Boolean = false,
        val drawerShown: Boolean = false,
        val listOfChatrooms: List<File> = emptyList(),
        val selectedChatroomTimestamp: Long? = null
    )
}