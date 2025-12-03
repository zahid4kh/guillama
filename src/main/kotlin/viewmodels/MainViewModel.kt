package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainViewModel(
    private val database: Database,
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val modelLibraryPath = File("/usr/share/ollama/.ollama/models/manifests/registry.ollama.ai/library")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = database.getSettings()
            _uiState.value = _uiState.value.copy(
                darkMode = settings.darkMode,
            )

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
        val modelsLibrary: List<String> = mutableListOf()
    )
}