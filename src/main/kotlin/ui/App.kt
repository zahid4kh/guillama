package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ui.screens.EntryScreen
import ui.theme.AppTheme
import viewmodels.MainViewModel
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import ui.screens.Chatroom
import viewmodels.ChatViewModel
import java.io.File


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
                    EntryScreen(
                        uiState = uiState,
                        mainViewModel = viewModel,
                        onCreateNewChatroom = {
                            chatViewModel.createChatroom()
                            navigator.navigate("/chatroom/${uiState.selectedChatroom?.createdAt}")
                        },
                        onNavigateToChatroom = { (chatroom, file) ->
                            viewModel.selectChatroom(chatroom = chatroom)
                            navigator.navigate("/chatroom/${uiState.selectedChatroom?.createdAt}")
                            chatViewModel.loadChatroom(chatroom = chatroom, file = file)
                        }
                    )
                }

                scene(route = "/chatroom/{createdAt}") {backStackEntry ->
                    val createdAt = backStackEntry.path<Long>("createdAt")
                    Chatroom(
                        chatUiState = chatUiState,
                        chatViewModel = chatViewModel,
                        onNavigateBackToHome = { navigator.goBack() }
                    )
                }
            }

        }
    }

}
