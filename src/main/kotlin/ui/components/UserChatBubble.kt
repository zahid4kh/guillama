package ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@Composable
fun UserChatBubble(
    modifier: Modifier,
    message: String = "Hello World???",
    createdAt: String = "05-12-2025; 14:62",
    isClicked: Boolean = false,
    onClick: () -> Unit
){
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .padding(10.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = { onClick() })
            .pointerHoverIcon(PointerIcon.Hand)
    ){
        OutlinedCard {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
        }

        AnimatedVisibility(
            visible = isClicked
        ){
            Text(
                text = createdAt,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(5.dp)
            )
        }

    }
}