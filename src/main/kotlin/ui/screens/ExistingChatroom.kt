package ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.Chatroom
import viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingChatroom(
    chatViewModel: ChatViewModel,
    chatUiState: ChatViewModel.ChatUiState,
    chatroom: Chatroom
){
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

                },
                actions = {
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
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .animateContentSize()
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    OutlinedTextField(
                        value = chatUiState.userMessage,
                        onValueChange = { chatViewModel.onUserMessageTyped(it) },
                        shape = MaterialTheme.shapes.medium,
                        placeholder = {
                            Text(
                                text = "Type your message..."
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .animateContentSize(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )

                    IconButton(
                        onClick = {  },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        enabled = chatUiState.selectedModel != null
                    ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Send message"
                        )
                    }
                }
            }

        }
    }
}