package viewmodels

import data.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MainViewModel(
    private val database: Database,
) {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            val settings = database.getSettings()
            _uiState.value = _uiState.value.copy(
                darkMode = settings.darkMode,
            )
        }
    }

    fun checkForAvailableModels(){
        scope.launch(Dispatchers.IO) {
            val command = listOf("ollama", "list")
            val pb = ProcessBuilder(command)
            val process = pb.start()
            val result = BufferedReader(InputStreamReader(process.inputStream))

            while(process.isAlive){
                if(result.ready()){
                    val line = result.readLine()
                    withContext(Dispatchers.Main){
                        _uiState.update { it.copy(availableModels = it.availableModels + line + "\n") }
                    }
                    println("Current line is: $line")
                }
            }
        }
    }

    fun clearOutput(){
        _uiState.update { it.copy(availableModels = "") }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.darkMode
        _uiState.value = _uiState.value.copy(darkMode = newDarkMode)

        scope.launch {
            val settings = database.getSettings()
            database.saveSettings(settings.copy(darkMode = newDarkMode))
        }
    }

    data class UiState(
        val darkMode: Boolean = false,
        val availableModels: String = ""
    )
}