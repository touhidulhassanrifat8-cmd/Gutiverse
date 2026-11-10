package com.example.gutiverse.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.gutiverse.communication.models.CallType
import com.example.gutiverse.communication.models.ChatMessage
import com.example.gutiverse.communication.models.DeliveryStatus
import com.example.gutiverse.communication.models.GutiverseFriend
import com.example.gutiverse.communication.models.MessageType
import com.example.gutiverse.communication.models.UserStatus
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseLaserCrimson
import com.example.ui.theme.GutiverseSuccessGreen
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrivateChatScreen(
    appViewModel: AppViewModel,
    friendId: String,
    modifier: Modifier = Modifier
) {
    BackHandler {
        appViewModel.navigateTo(AppScreen.MESSAGES)
    }

    val friends by appViewModel.communicationRepository.friends.collectAsState()
    val blockedIds by appViewModel.communicationRepository.blockedUserIds.collectAsState()
    val allMessages by appViewModel.communicationRepository.chatMessages.collectAsState()
    val messages = allMessages[friendId].orEmpty()

    val friend = friends.firstOrNull { it.id == friendId } ?: GutiverseFriend(
        id = friendId,
        name = "Player_${friendId.takeLast(4)}",
        status = UserStatus.ONLINE
    )

    val isBlocked = blockedIds.contains(friendId)

    var inputText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<ChatMessage?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var showEmojiRow by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Chat Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(GutiverseSurfaceDark)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { appViewModel.navigateTo(AppScreen.MESSAGES) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GutiverseGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                        contentDescription = friend.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (isBlocked) "Blocked" else when (friend.status) {
                            UserStatus.ONLINE -> "Online • Active Now"
                            UserStatus.IN_GAME -> "In 16 Guti Match"
                            UserStatus.OFFLINE -> friend.lastSeenText
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (friend.status == UserStatus.ONLINE) GutiverseSuccessGreen else Color.Gray,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Audio & Video Call buttons (imo-style direct actions)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        appViewModel.startCall(friend.id, friend.name, CallType.AUDIO)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Audio Call", tint = GutiverseGold)
                }

                IconButton(
                    onClick = {
                        appViewModel.startCall(friend.id, friend.name, CallType.VIDEO)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = GutiverseCyan)
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (friend.isFavorite) "Remove from Favorites" else "Add to Favorites") },
                            onClick = {
                                appViewModel.communicationRepository.toggleFavorite(friend.id)
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isBlocked) "Unblock Player" else "Block Player", color = GutiverseLaserCrimson) },
                            onClick = {
                                appViewModel.communicationRepository.toggleBlock(friend.id)
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == "GUTI-7894-BD"
                MessageBubble(
                    message = msg,
                    isMe = isMe,
                    onReply = { replyingTo = msg },
                    onDelete = { appViewModel.communicationRepository.deleteMessage(friendId, msg.id) }
                )
            }
        }

        // Reply Preview Bar
        AnimatedVisibility(visible = replyingTo != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GutiverseSurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column {
                    Text("Replying to ${replyingTo?.senderName}:", style = MaterialTheme.typography.labelSmall.copy(color = GutiverseCyan))
                    Text(replyingTo?.text.orEmpty(), maxLines = 1, style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray))
                }
                Text("✕", color = Color.White, modifier = Modifier.clickable { replyingTo = null })
            }
        }

        // Quick Emoji Selector Row
        AnimatedVisibility(visible = showEmojiRow) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GutiverseSurfaceDark)
                    .padding(vertical = 8.dp)
            ) {
                listOf("❤️", "🔥", "🏆", "🎮", "😂", "👍", "⚔️", "👑").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.clickable {
                            appViewModel.communicationRepository.sendMessage(
                                friendId = friendId,
                                text = emoji,
                                type = MessageType.STICKER
                            )
                            appViewModel.soundManager.playButtonClick()
                            showEmojiRow = false
                        }
                    )
                }
            }
        }

        // Bottom Input Deck
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(GutiverseSurfaceDark)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            // Emoji toggle
            IconButton(onClick = { showEmojiRow = !showEmojiRow }) {
                Text("😀", fontSize = 20.sp)
            }

            // Photo attach simulation
            IconButton(
                onClick = {
                    appViewModel.communicationRepository.sendMessage(
                        friendId = friendId,
                        text = "📷 Shared a photo: 16 Guti Victory Board",
                        type = MessageType.PHOTO
                    )
                    appViewModel.soundManager.playButtonClick()
                }
            ) {
                Icon(imageVector = Icons.Default.Image, contentDescription = "Send Photo", tint = GutiverseGold)
            }

            // Voice Note Simulation
            IconButton(
                onClick = {
                    // Send 4-second voice message
                    appViewModel.communicationRepository.sendMessage(
                        friendId = friendId,
                        text = "🎙️ Voice Message (0:04)",
                        type = MessageType.VOICE,
                        voiceDurationSeconds = 4
                    )
                    appViewModel.soundManager.playButtonClick()
                }
            ) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Note", tint = GutiverseCyan)
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Message...", color = Color.Gray, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GutiverseGold,
                    unfocusedBorderColor = Color(0xFF3B2C63),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            appViewModel.communicationRepository.sendMessage(
                                friendId = friendId,
                                text = inputText,
                                replyToId = replyingTo?.id,
                                replyToText = replyingTo?.text
                            )
                            inputText = ""
                            replyingTo = null
                            appViewModel.soundManager.playButtonClick()
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        appViewModel.communicationRepository.sendMessage(
                            friendId = friendId,
                            text = inputText,
                            replyToId = replyingTo?.id,
                            replyToText = replyingTo?.text
                        )
                        inputText = ""
                        replyingTo = null
                        appViewModel.soundManager.playButtonClick()
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GutiverseGold)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = GutiverseDarkVoid)
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    onReply: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeStr = formatter.format(Date(message.timestampMillis))

    Column(
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) Color(0xFF261947) else GutiverseSurfaceElevated
            ),
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isMe) 14.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 14.dp
            ),
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (isMe) GutiverseGold.copy(alpha = 0.4f) else Color(0xFF3A2B5E),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { onReply() }
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Reply quote if present
                if (message.replyToText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GutiverseDarkVoid.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Replying: ${message.replyToText}",
                            style = MaterialTheme.typography.labelSmall.copy(color = GutiverseCyan, fontSize = 9.sp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Message Body
                when (message.type) {
                    MessageType.TEXT, MessageType.STICKER -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontSize = if (message.type == MessageType.STICKER) 24.sp else 13.sp
                            )
                        )
                    }
                    MessageType.PHOTO -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(color = GutiverseGold, fontWeight = FontWeight.Bold)
                        )
                    }
                    MessageType.VOICE -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = GutiverseCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Voice Note (${message.voiceDurationSeconds}s)",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontSize = 12.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time and delivery receipts (sent / delivered / read ticks)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 9.sp)
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        when (message.deliveryStatus) {
                            DeliveryStatus.SENDING -> {
                                Text("⏱", fontSize = 9.sp)
                            }
                            DeliveryStatus.SENT -> {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Sent", tint = Color.Gray, modifier = Modifier.size(12.dp))
                            }
                            DeliveryStatus.DELIVERED -> {
                                Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Delivered", tint = Color.Gray, modifier = Modifier.size(12.dp))
                            }
                            DeliveryStatus.READ -> {
                                Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Read", tint = GutiverseCyan, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
