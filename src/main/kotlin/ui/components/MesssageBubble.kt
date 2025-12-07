package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.GenericMessage

@Composable
fun MessageBubble(
    message: GenericMessage
){
    val isUser = message.role == "user"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (!isUser) 90.dp else 0.dp)
            .padding(horizontal = 15.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if(isUser){
            OutlinedCard{
                Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }
        }else{
            SelectionContainer {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }

        }

    }
}