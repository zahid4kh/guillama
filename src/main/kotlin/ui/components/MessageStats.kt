package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ui.theme.getJetbrainsMonoFamily
import viewmodels.ChatViewModel

@Composable
fun MessageStats(messageStats: ChatViewModel.MessageStats){
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(5.dp)
    ) {
        Text(
            text = "Created at: ${messageStats.createdAt}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )

        Text(
            text = "Responded in: ${messageStats.totalDuration}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )

        Text(
            text = "Model loaded in: ${messageStats.loadDuration}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )

        Text(
            text = "Input token count: ${messageStats.promptEvalCount}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )

        Text(
            text = "Input evaluated in: ${messageStats.promptEvalDuration}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )

        Text(
            text = "Output token count: ${messageStats.evalCount}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )

        Text(
            text = "Output generated in: ${messageStats.evalDuration}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )

        Text(
            text = "Speed: ${messageStats.generaationSpeed} token/s",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = getJetbrainsMonoFamily()
        )
    }
}