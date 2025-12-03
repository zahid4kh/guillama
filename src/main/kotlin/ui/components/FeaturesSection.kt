package ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeaturesSection(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){
        FeatureCard(
            icon = Icons.Outlined.FlashOn,
            text = "Lightning Fast",
            description = "Run powerful LLMs locally with\noptimized performance",
            modifier = Modifier.weight(1f)
        )

        FeatureCard(
            icon = Icons.Outlined.Shield,
            text = "Private & Secure",
            description = "Your conversations stay on your machine,\ncompletely private",
            modifier = Modifier.weight(1f)
        )

        FeatureCard(
            icon = Icons.Outlined.Download,
            text = "Multiple Models",
            description = "Switch between different Ollama\nmodels seamlessly",
            modifier = Modifier.weight(1f)
        )
    }
}
