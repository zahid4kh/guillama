@file:JvmName("GUILLAMA")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import api.OllamaApi
import di.appModule
import moe.tlaster.precompose.ProvidePreComposeLocals
import ui.theme.AppTheme
import java.awt.Dimension
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import ui.App
import viewmodels.ChatViewModel
import viewmodels.MainViewModel


fun main() = application {
    startKoin {
        modules(appModule)
    }

    val viewModel = getKoin().get<MainViewModel>()
    val chatViewModel = getKoin().get<ChatViewModel>()
    val windowState = rememberWindowState(size = DpSize(800.dp, 600.dp))
    val api = OllamaApi()

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        alwaysOnTop = true,
        title = "GUILLAMA",
        icon = null
    ) {
        window.minimumSize = Dimension(800, 600)

        ProvidePreComposeLocals {
            App(
                viewModel = viewModel,
                chatViewModel = chatViewModel
            )
        }
    }
}