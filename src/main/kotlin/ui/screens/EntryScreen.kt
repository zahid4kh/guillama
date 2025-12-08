package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.Chatroom
import ui.components.FeaturesSection
import ui.components.ModelAvailabilityCountCard
import viewmodels.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    uiState: MainViewModel.UiState,
    mainViewModel: MainViewModel,
    onCreateNewChatroom: () -> Unit,
    onNavigateToChatroom: (Pair<Chatroom, File>) -> Unit
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    LaunchedEffect(uiState.drawerShown){
        drawerState.apply {
            if(uiState.drawerShown) {
                open()
            } else close()
        }
        mainViewModel.listChatRooms()
    }

    SideDrawer(
        uiState = uiState,
        mainViewModel = mainViewModel,
        drawerState = drawerState,
        onNavigateToChatroom = onNavigateToChatroom
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = "GUILLAMA",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Your local AI assistant powered by Ollama models",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FeaturesSection()

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onCreateNewChatroom() },
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .size(240.dp, 56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubble,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Start New Chat",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (uiState.modelsLibrary.isNotEmpty()) {
                    ModelAvailabilityCountCard(
                        count = uiState.modelsLibrary.size,
                        onClick = { mainViewModel.showModelListDialog() }
                    )
                }
            }

            AnimatedVisibility(
                visible = !uiState.drawerShown,
                modifier = Modifier.align(Alignment.TopStart),
                exit = scaleOut(),
                enter = scaleIn()
            ) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                    tooltip = {
                        PlainTooltip { Text("Open menu", style = MaterialTheme.typography.bodyMedium) }
                    },
                    state = rememberTooltipState()
                ) {
                    FloatingActionButton(
                        onClick = { mainViewModel.showSideDrawer() },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open menu"
                        )
                    }
                }
            }
        }
    }

    if (uiState.modelListDialogShown) {
        AlertDialog(
            onDismissRequest = { mainViewModel.closeModelListDialog() },
            dismissButton = {
                OutlinedButton(
                    onClick = { mainViewModel.closeModelListDialog() },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
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
                    text = "Available Models",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(uiState.modelsLibrary) { model ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = model,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        )
    }
}