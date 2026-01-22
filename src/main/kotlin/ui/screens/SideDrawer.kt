package ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.Chatroom
import kotlinx.coroutines.delay
import viewmodels.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideDrawer(
    uiState: MainViewModel.UiState,
    mainViewModel: MainViewModel,
    drawerState: DrawerState,
    onNavigateToChatroom: (Pair<Chatroom, File>) -> Unit,
    onDeleteChatroom: (Pair<Chatroom, File>) -> Unit,
    content: @Composable (() -> Unit)
){
    val lazyListState = rememberLazyListState()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.widthIn(min = 280.dp, max = 320.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GUILLAMA",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                        tooltip = {
                            PlainTooltip { Text("Close menu", style = MaterialTheme.typography.bodyMedium) }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = { mainViewModel.closeSideDrawer() },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = "Close menu"
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "SETTINGS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { mainViewModel.toggleDarkMode() }
                            .padding(12.dp)
                            .pointerHoverIcon(PointerIcon.Hand),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme toggle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.darkMode) "Dark Theme" else "Light Theme",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Switch appearance",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.darkMode,
                            onCheckedChange = { mainViewModel.toggleDarkMode() }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHATS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                        tooltip = {
                            PlainTooltip { Text("Reload chatrooms", style = MaterialTheme.typography.bodyMedium) }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = { mainViewModel.listChatRooms() },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Update,
                                contentDescription = "Reload chatrooms"
                            )
                        }
                    }
                }


                Box{
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        state = lazyListState
                    ) {
                        items(items = uiState.listOfChatroomsWithFiles) { (chatroom, file) ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val isItemHovered by interactionSource.collectIsHoveredAsState()
                            var scale by remember { mutableStateOf(1f) }
                            val animatedScale by animateFloatAsState(
                                targetValue = if(isItemHovered) 1.1f else scale,
                                animationSpec = tween(easing = LinearOutSlowInEasing)
                            )
                            ElevatedCard(
                                onClick = { onNavigateToChatroom(chatroom to file) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .hoverable(interactionSource)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .scale(animatedScale)
                                    .pointerHoverIcon(PointerIcon.Hand),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = chatroom.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 2
                                        )
                                        if (chatroom.modelInThisChatroom != null) {
                                            Text(
                                                text = "Model: ${chatroom.modelInThisChatroom}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = isItemHovered,
                                        enter = scaleIn(animationSpec = tween(delayMillis = 300)),
                                        exit = scaleOut(animationSpec = tween(delayMillis = 300))
                                    ){
                                        IconButton(
                                            onClick = { onDeleteChatroom(chatroom to file) }
                                        ){
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete chatroom",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                }

                            }
                        }
                    }

                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(lazyListState),
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 4.dp)
                            .align(Alignment.TopEnd)
                            .pointerHoverIcon(PointerIcon.Hand)
                    )
                }

            }
        },
        drawerState = drawerState,
        modifier = Modifier.fillMaxSize()
    ){
        content()
    }
}