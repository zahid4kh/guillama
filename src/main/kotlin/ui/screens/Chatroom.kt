package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.components.MessageInputCard
import ui.components.OllamaChatBubble
import ui.components.UserChatBubble
import viewmodels.ChatViewModel
import java.awt.SystemColor.text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chatroom(
    chatViewModel: ChatViewModel,
    chatUiState: ChatViewModel.ChatUiState,
    onNavigateBackToHome: () -> Unit
){
    var isUserBubbleClicked by remember { mutableStateOf(false) }
    var isOllamaBubbleClicked by remember { mutableStateOf(false) }

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
                modifier = Modifier.matchParentSize()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ){
                        AnimatedVisibility(
                            visible = chatUiState.userMessage.isNotEmpty()
                        ){
                            UserChatBubble(
                                modifier = Modifier,
                                onClick = { isUserBubbleClicked = !isUserBubbleClicked },
                                isClicked = isUserBubbleClicked,
                                message = chatUiState.userMessage
                            )
                        }

                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ){
                        AnimatedVisibility(visible = !chatUiState.modelMessage.isNullOrBlank()){
                            OllamaChatBubble(
                                modifier = Modifier,
                                onClick = { isOllamaBubbleClicked = !isOllamaBubbleClicked },
                                isClicked = isOllamaBubbleClicked,
                                message = chatUiState.modelMessage?:""
                            )
                        }

                    }
                }
            }


            MessageInputCard(
                chatUiState = chatUiState,
                chatViewModel = chatViewModel,
                modifier = Modifier.align(Alignment.BottomCenter),
                onSendMessage = {  }
            )
        }
    }
}