package ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import guillaama.resources.Res
import guillaama.resources.messages_square
import org.jetbrains.compose.resources.painterResource

@Composable
fun EntryScreen(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column {
            Icon(
                painter = painterResource(Res.drawable.messages_square),
                contentDescription = null
            )
        }
    }
}