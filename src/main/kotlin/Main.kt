@file:JvmName("GUILLAMA")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import api.OllamaApi
import di.appModule
import guillaama.resources.Res
import guillaama.resources.aappIcon
import moe.tlaster.precompose.ProvidePreComposeLocals
import org.jetbrains.compose.resources.painterResource
import ui.theme.AppTheme
import java.awt.Dimension
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin
import ui.App
import viewmodels.ChatViewModel
import viewmodels.MainViewModel


fun main() = application {
    startKoin {
        modules(appModule)
    }

    val api = OllamaApi()
    val viewModel = getKoin().get<MainViewModel> { parametersOf(api) }
    val chatViewModel = getKoin().get<ChatViewModel>()
    val windowState = rememberWindowState(size = DpSize(800.dp, 600.dp))

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        alwaysOnTop = true,
        title = "GUILLAMA",
        icon = painterResource(Res.drawable.aappIcon)
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