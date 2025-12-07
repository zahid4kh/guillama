package ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import data.GenericMessage

@Composable
fun MessageBubble(
    message: GenericMessage,
    modifier: Modifier,
    isStreaming: Boolean = false
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
            ElevatedCard(
                modifier = Modifier.padding(vertical = 10.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 10.dp
                )
            ) {
                SelectionContainer {
                    if (isStreaming) {
                        AnimatedTokenText(
                            text = message.content,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
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