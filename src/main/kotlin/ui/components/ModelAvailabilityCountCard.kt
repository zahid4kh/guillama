package ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import ui.theme.getJetbrainsMonoFamily

@Composable
fun ModelAvailabilityCountCard(
    count: Int,
    onClick: () -> Unit
){
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val size by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "size"
    )
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .clip(MaterialTheme.shapes.medium)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = { onClick() })
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Canvas(modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
            ){
                drawCircle(
                    color = Color.Green,
                    alpha = alpha,
                    radius = size
                )
            }

            Text(
                text = "$count ollama models detected",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = getJetbrainsMonoFamily(),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}