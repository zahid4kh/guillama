package ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ui.components.FeatureCard
import ui.components.FeaturesSection
import ui.components.ModelAvailabilityCountCard
import viewmodels.MainViewModel
import javax.swing.Icon

@Composable
fun EntryScreen(
    uiState: MainViewModel.UiState,
    mainViewModel: MainViewModel
){
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
            ){
                Text(
                    text = "CHATS",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = {  }
                )

            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Text(
                    text = "Ollama Chat Desktop",
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = "Your local AI assistant powered by Ollama models",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(20.dp))
                FeaturesSection()

                Spacer(modifier = Modifier.height(60.dp))
                Button(
                    onClick = {  },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .size(220.dp, 60.dp)
                ){
                    Icon(
                        imageVector = Icons.Outlined.ChatBubble,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "Start Chatting",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                ModelAvailabilityCountCard(
                    count = uiState.modelsLibrary.size,
                    onClick = { mainViewModel.showModelListDialog() }
                )
            }
        }
    }

    if(uiState.modelListDialogShown){
        AlertDialog(
            onDismissRequest = { mainViewModel.closeModelListDialog() },
            dismissButton = {
                OutlinedButton(
                    onClick = { mainViewModel.closeModelListDialog() },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ){
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {},
            title = {
                Text(
                    text = "Available models",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    uiState.modelsLibrary.forEach { model ->
                        Text(
                            text = model
                        )
                    }
                }


            }
        )
    }
}