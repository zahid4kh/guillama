package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.components.MessageBubble
import ui.components.MessageInputCard
import viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chatroom(
    chatViewModel: ChatViewModel,
    chatUiState: ChatViewModel.ChatUiState,
    onNavigateBackToHome: () -> Unit
){
    val listState = rememberLazyListState()
    LaunchedEffect(chatUiState.messages.size) {
        if (chatUiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedContent(
                            targetState = chatUiState.isEditingTitle
                        ){ isEditing ->
                            if(isEditing){
                                OutlinedTextField(
                                    value = chatUiState.chatRoomTitle,
                                    onValueChange = { chatViewModel.saveNewTitle(it) },
                                    shape = MaterialTheme.shapes.medium,
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )
                            }else{
                                Text(
                                    text = chatUiState.chatRoomTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false
                                )
                            }
                        }

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip { Text(if(chatUiState.isEditingTitle) "Save title" else "Rename title", style = MaterialTheme.typography.bodyMedium) }
                            },
                            state = rememberTooltipState()
                        ){
                            IconButton(
                                onClick = { chatViewModel.handleEditSaveTitle() }
                            ){
                                Icon(
                                    imageVector = if(chatUiState.isEditingTitle) Icons.Default.CheckCircle else Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                    }

                },
                actions = {
                    AnimatedVisibility(
                        visible = chatUiState.message.isNotEmpty(),
                    ){
                        OutlinedCard(
                            modifier = Modifier.padding(10.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = Color.Green.copy(alpha = 0.6f)
                            )
                        ) {
                            Text(
                                text = chatUiState.message?:"",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text("Save changes", style = MaterialTheme.typography.bodyMedium) }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = { chatViewModel.updateSaveChatroom() },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Save,
                                contentDescription = "Save or update this chatroom"
                            )
                        }
                    }

                    if(chatUiState.selectedModel == null){
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                        ){
                            IconButton(
                                onClick = { chatViewModel.expandDropdown() }
                            ){
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Select a model"
                                )
                            }
                            DropdownMenu(
                                expanded = chatUiState.showModelSelectorDropdown,
                                onDismissRequest = { chatViewModel.closeDropdown() },
                                shape = MaterialTheme.shapes.medium,
                                shadowElevation = 10.dp,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ){
                                chatUiState.availableModels.forEach { modelName ->
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .pointerHoverIcon(PointerIcon.Hand)
                                            .clip(MaterialTheme.shapes.medium),
                                        text = {
                                            Text(
                                                text = modelName
                                            )
                                        },
                                        onClick = { chatViewModel.onSelectModel(modelName) }
                                    )
                                }

                            }
                        }
                    }else{
                        Text(
                            text = "Model in this chat: ${chatUiState.selectedModel}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                },
                navigationIcon = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text("Go Home", style = MaterialTheme.typography.bodyMedium) }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = { onNavigateBackToHome() },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Go back to entry screen"
                            )
                        }
                    }

                },
                modifier = Modifier.clip(
                    RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            LazyColumn(
                state = listState,
                modifier = Modifier.matchParentSize(),
                reverseLayout = true
            ) {
                itemsIndexed(items = chatUiState.messages) { index, message ->
                    val isLastMessage = index == 0
                    val isStreamingThisMessage = isLastMessage &&
                            message.role == "assistant" &&
                            chatUiState.isStreaming

                    MessageBubble(
                        message = message,
                        modifier = Modifier.offset(y = -90.dp),
                        isStreaming = isStreamingThisMessage
                    )
                }
            }

            MessageInputCard(
                chatUiState = chatUiState,
                chatViewModel = chatViewModel,
                modifier = Modifier.align(Alignment.BottomCenter),
                onSendMessage = { chatViewModel.sendMessage() }
            )
        }
    }
}