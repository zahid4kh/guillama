package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import data.Chatroom
import ui.components.FeatureCard
import ui.components.FeaturesSection
import ui.components.ModelAvailabilityCountCard
import viewmodels.ChatViewModel
import viewmodels.MainViewModel
import java.io.File
import javax.swing.Icon

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
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHATS",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                    contentDescription = null
                                )
                            }
                        }

                    }

                }

                HorizontalDivider()
                LazyColumn {
                    items(items = uiState.listOfChatroomsWithFiles){ (chatroom, file) ->
                        TextButton(
                            onClick = { onNavigateToChatroom(chatroom to file) },
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Text(
                                text = chatroom.title,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        drawerState = drawerState,
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

                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip { Text("Create a new chatroom", style = MaterialTheme.typography.bodyMedium) }
                    },
                    state = rememberTooltipState()
                ){
                    Button(
                        onClick = { onCreateNewChatroom() },
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
                }


                if(uiState.modelsLibrary.isNotEmpty()){
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
            ){
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip { Text("Open menu", style = MaterialTheme.typography.bodyMedium) }
                    },
                    state = rememberTooltipState()
                ){
                    IconButton(
                        onClick = { mainViewModel.showSideDrawer() },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ){
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null
                        )
                    }
                }

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