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
import moe.tlaster.precompose.navigation.rememberNavigator
import ui.screens.NewChatroom
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
                    EntryScreen(
                        uiState = uiState,
                        mainViewModel = viewModel,
                        onCreateNewChatroom = {
                            navigator.navigate("/chatroom")
                            chatViewModel.createChatroom()
                        },
                        onNavigateToChatroom = {
                            viewModel.selectChatroom(chatroom = it)
                            navigator.navigate("/chatroom/${uiState.selectedChatroomTimestamp}")
                        }
                    )
                }

                scene(route = "/chatroom"){
                    NewChatroom(
                        chatUiState = chatUiState,
                        chatViewModel = chatViewModel
                    )
                }
            }

        }
    }

}
