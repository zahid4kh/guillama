package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ui.screens.EntryScreen
import ui.theme.AppTheme
import viewmodels.MainViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import viewmodels.ChatViewModel


@Composable
@Preview
fun App(
    viewModel: MainViewModel,
    chatViewModel: ChatViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val chatUiState by chatViewModel.chatUiState.collectAsState()

    PreComposeApp {
        val navigator = rememberNavigator()
        AppTheme(darkTheme = uiState.darkMode) {
            NavHost(
                navigator = navigator,
                initialRoute = "/home"
            ){
                scene(route = "/home"){
                    EntryScreen(uiState, viewModel)
                }

                scene(route = "/chatroom"){
                    Chatroom(
                        chatUiState = chatUiState,
                        chatViewModel = chatViewModel
                    )
                }
            }

        }
    }

}
