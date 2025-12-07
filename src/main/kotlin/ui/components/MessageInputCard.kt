package ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputCard(
    chatViewModel: ChatViewModel,
    chatUiState: ChatViewModel.ChatUiState,
    modifier: Modifier,
    onSendMessage: () -> Unit
){
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(3.dp)
            .animateContentSize()
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

            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip { Text("Send message", style = MaterialTheme.typography.bodySmall) }
                },
                state = rememberTooltipState()
            ){
                IconButton(
                    onClick = { onSendMessage() },
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