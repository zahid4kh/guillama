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

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.widthIn(min = 280.dp, max = 320.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GUILLAMA",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text("Close menu", style = MaterialTheme.typography.bodyMedium) }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = { mainViewModel.closeSideDrawer() },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = "Close menu"
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "SETTINGS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { mainViewModel.toggleDarkMode() }
                            .padding(12.dp)
                            .pointerHoverIcon(PointerIcon.Hand),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme toggle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.darkMode) "Dark Theme" else "Light Theme",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Switch appearance",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.darkMode,
                            onCheckedChange = { mainViewModel.toggleDarkMode() }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHATS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text("Reload chatrooms", style = MaterialTheme.typography.bodyMedium) }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = { mainViewModel.listChatRooms() },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Update,
                                contentDescription = "Reload chatrooms"
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    items(items = uiState.listOfChatroomsWithFiles) { (chatroom, file) ->
                        ElevatedCard(
                            onClick = { onNavigateToChatroom(chatroom to file) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .pointerHoverIcon(PointerIcon.Hand),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = chatroom.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2
                                )
                                if (chatroom.modelInThisChatroom != null) {
                                    Text(
                                        text = "Model: ${chatroom.modelInThisChatroom}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        drawerState = drawerState,
        modifier = Modifier.fillMaxSize()
    ) {
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
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
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