package ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import data.GenericMessage
import ui.theme.getJetbrainsMonoFamily
import viewmodels.ChatViewModel

@Composable
fun MessageBubble(
    message: GenericMessage,
    modifier: Modifier,
    chatUiState: ChatViewModel.ChatUiState,
    chatViewModel: ChatViewModel,
    isStreaming: Boolean = false,
    isLastMessage: Boolean,
    stats: ChatViewModel.MessageStats
){
    val isUser = message.role == "user"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if(isUser){
            OutlinedCard{
                Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }else{
            Card(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(
                        onClick = { chatViewModel.toggleMessageStats() },
                        enabled = isLastMessage
                    )
                    .pointerHoverIcon(if(isLastMessage) PointerIcon.Hand else PointerIcon.Default),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                if (isStreaming) {
                    AnimatedTokenText(
                        text = message.content,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Column {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        AnimatedVisibility(
                            visible = chatUiState.showMessageStats && isLastMessage
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(5.dp)
                            ) {
                                Text(
                                    text = "Created at: ${stats.createdAt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = getJetbrainsMonoFamily()
                                )

                                Text(
                                    text = "Total Duration: ${stats.totalDuration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = getJetbrainsMonoFamily()
                                )

                                Text(
                                    text = "Load Duration: ${stats.loadDuration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = getJetbrainsMonoFamily()
                                )

                                Text(
                                    text = "Prompt Eval. Count: ${stats.promptEvalCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = getJetbrainsMonoFamily()
                                )

                                Text(
                                    text = "Prompt Eval. Duration: ${stats.promptEvalDuration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = getJetbrainsMonoFamily()
                                )

                                Text(
                                    text = "Eval. Count: ${stats.evalCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = getJetbrainsMonoFamily()
                                )

                                Text(
                                    text = "Eval. Duration: ${stats.evalDuration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = getJetbrainsMonoFamily()
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun AnimatedTokenText(
    text: String,
    modifier: Modifier = Modifier
) {
    var lastText by remember { mutableStateOf("") }
    var shouldAnimate by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (shouldAnimate) 0.7f else 1f,
        animationSpec = tween(150),
        finishedListener = {
            if (shouldAnimate) {
                shouldAnimate = false
            }
        },
        label = "tokenFade"
    )

    LaunchedEffect(text) {
        if (text != lastText && text.length > lastText.length) {
            shouldAnimate = true
        }
        lastText = text
    }

    Text(
        text = text,
        modifier = modifier
            .animateContentSize()
            .graphicsLayer { this.alpha = alpha },
        style = MaterialTheme.typography.bodyMedium
    )
}