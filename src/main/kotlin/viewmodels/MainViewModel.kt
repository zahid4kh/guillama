package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.OllamaApi
import data.Chatroom
import data.Database
import data.ModelsResponse
import data.OllamaModel
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
    private val ollamaApi: OllamaApi
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val homeDir = System.getProperty("user.home")
    val chatsDir = File("$homeDir/.guillama/chats")
    private val modelsDir = File("$homeDir/.guillama/models")

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
            fetchAndCacheModels()
        }
    }

    private fun fetchAndCacheModels() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!modelsDir.exists()) {
                    modelsDir.mkdirs()
                }

                val modelsResponse = ollamaApi.getAvailableModels()

                if (modelsResponse != null && modelsResponse.models.isNotEmpty()) {
                    val modelsFile = File(modelsDir, "models.json")
                    modelsFile.writeText(json.encodeToString<ModelsResponse>(modelsResponse))

                    val modelNames = modelsResponse.models.map { it.name }

                    _uiState.update {
                        it.copy(
                            modelsLibrary = modelNames,
                            availableModels = modelsResponse.models
                        )
                    }

                    println("Fetched ${modelNames.size} models from Ollama API")
                } else {
                    loadCachedModels()
                }
            } catch (e: Exception) {
                println("Error fetching models: $e")
                loadCachedModels()
            }
        }
    }

    private fun loadCachedModels() {
        try {
            val modelsFile = File(modelsDir, "models.json")
            if (modelsFile.exists()) {
                val cachedResponse = json.decodeFromString<data.ModelsResponse>(modelsFile.readText())
                val modelNames = cachedResponse.models.map { it.name }

                _uiState.update {
                    it.copy(
                        modelsLibrary = modelNames,
                        availableModels = cachedResponse.models
                    )
                }

                println("Loaded ${modelNames.size} cached models")
            } else {
                println("No cached models found")
            }
        } catch (e: Exception) {
            println("Error loading cached models: $e")
        }
    }

    fun refreshModels() {
        fetchAndCacheModels()
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
                val files = chatsDir.listFiles()
                val listOfChatrooms = mutableListOf<Pair<Chatroom, File>>()
                files?.forEach { file ->
                    try {
                        val decodedString = json.decodeFromString<Chatroom>(file.readText())
                        listOfChatrooms.add(decodedString to file)
                    } catch (e: Exception) {
                        println("Error reading chatroom file ${file.name}: $e")
                    }
                }
                _uiState.update {
                    it.copy(listOfChatroomsWithFiles = listOfChatrooms)
                }
            }
        }
    }

    fun selectChatroom(chatroom: Chatroom){
        _uiState.update { it.copy(selectedChatroom = chatroom) }
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
        val modelsLibrary: List<String> = emptyList(),
        val availableModels: List<OllamaModel> = emptyList(),
        val modelListDialogShown: Boolean = false,
        val drawerShown: Boolean = false,
        val listOfChatroomsWithFiles: List<Pair<Chatroom, File>> = emptyList(),
        val selectedChatroom: Chatroom? = null
    )
}