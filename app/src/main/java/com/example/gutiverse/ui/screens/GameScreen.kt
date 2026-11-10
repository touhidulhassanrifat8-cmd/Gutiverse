package com.example.gutiverse.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.gutiverse.communication.models.CallSession
import com.example.gutiverse.communication.models.CallStatus
import com.example.gutiverse.communication.models.CallType
import com.example.gutiverse.engine.PlayerSide
import com.example.gutiverse.engine.RulesConfiguration
import com.example.gutiverse.localization.AppLanguage
import com.example.gutiverse.ui.components.BoardCanvas
import com.example.gutiverse.ui.components.LiveFloatingVideoOverlay
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseLaserCrimson
import com.example.ui.theme.GutiverseSuccessGreen
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel
import com.example.gutiverse.viewmodel.GameMode
import com.example.gutiverse.viewmodel.GameViewModel

@Composable
fun GameScreen(
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val gameViewModel = remember {
        GameViewModel(
            gameMode = appViewModel.gameMode.value,
            botDifficulty = appViewModel.botDifficulty.value,
            rules = appViewModel.rulesConfig.value,
            soundManager = appViewModel.soundManager,
            mascotVoiceManager = appViewModel.mascotVoiceManager
        )
    }

    val gameState by gameViewModel.gameState.collectAsState()
    val turnTimer by gameViewModel.turnTimeRemaining.collectAsState()
    val matchTimer by gameViewModel.matchTimeRemaining.collectAsState()
    val language by appViewModel.language.collectAsState()

    // Live Video Calling & Profile States
    val activeCall by appViewModel.callManager.currentCall.collectAsState()
    val isVideoMinimized by appViewModel.callManager.isVideoMinimized.collectAsState()

    var showRulesDialog by remember { mutableStateOf(false) }
    var showSurrenderConfirm by remember { mutableStateOf(false) }

    // Intercept back button to return to home safely without breaking active game
    BackHandler {
        appViewModel.navigateTo(AppScreen.HOME)
    }

    val p1BeadsAlive = gameState.board.values.count { it == PlayerSide.PLAYER_ONE }
    val p2BeadsAlive = gameState.board.values.count { it == PlayerSide.PLAYER_TWO }

    val isPlayerOneTurn = gameState.activePlayer == PlayerSide.PLAYER_ONE
    val isMultiJumpActive = gameState.activeJumpingNodeId != null

    val matchTimerMin = matchTimer / 60
    val matchTimerSec = matchTimer % 60

    // Opponent Identity configuration
    val p2Name = if (gameViewModel.isVsAi) "GutiBot AI" else "Tanvir_GutiKing"
    val p2Id = if (gameViewModel.isVsAi) "GUTI-BOT-9999" else "GUTI-1024-DH"
    val p1Name = "Touhidul_Rifat"
    val p1Id = "GUTI-7894-BD"

    val isCallConnected = activeCall != null && activeCall?.status == CallStatus.CONNECTED
    val isCallIncoming = activeCall != null && activeCall?.status == CallStatus.INCOMING

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF060818))
    ) {
        Image(
            painter = painterResource(id = R.drawable.robotic_machinery_bg_1794304375888),
            contentDescription = "Robotic Machinery Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF060818).copy(alpha = 0.48f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
        // TOP HEADER BAR: Back Button + Match Status / In-Game Call Trigger + Restart Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp)
        ) {
            // Top-Left Circular Back Button
            IconButton(
                onClick = {
                    appViewModel.soundManager.playButtonClick()
                    appViewModel.navigateTo(AppScreen.HOME)
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF28244E))
                    .border(1.dp, Color(0xFF5D528F), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Central Header: Match Timer & Dedicated In-Game Video Call Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                // In-Game Video Call Trigger Button
                Button(
                    onClick = {
                        if (!isCallConnected) {
                            appViewModel.callManager.startOutgoingCall(
                                peerId = p2Id,
                                peerName = p2Name,
                                type = CallType.VIDEO
                            )
                        } else {
                            appViewModel.callManager.setVideoMinimized(false)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCallConnected) GutiverseSuccessGreen.copy(alpha = 0.2f) else GutiverseSurfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.border(
                        1.dp,
                        if (isCallConnected) GutiverseSuccessGreen else GutiverseCyan,
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Live Video",
                        tint = if (isCallConnected) GutiverseSuccessGreen else GutiverseCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCallConnected) "Live Video 📹" else "Video Call 📹",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Match Timer
                Text(
                    text = String.format("%02d:%02d", matchTimerMin, matchTimerSec),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GutiverseGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                )
            }

            // Top-Right Circular Restart Button
            IconButton(
                onClick = {
                    appViewModel.soundManager.playButtonClick()
                    gameViewModel.restartMatch()
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF28244E))
                    .border(1.dp, Color(0xFF5D528F), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 1. TOP SIDE: PLAYER TWO PROFILE PANEL (Rich live profile with photo, username, ID, beads, score, and video status)
        PlayerProfilePanel(
            playerName = p2Name,
            playerId = p2Id,
            beadsAlive = p2BeadsAlive,
            score = gameState.playerTwoCaptured,
            beadColor = Color(0xFFFF9100),
            avatarResId = if (gameViewModel.isVsAi) R.drawable.gutibot_mascot_1794220235371 else R.drawable.gutiverse_icon_1794220208403,
            isTurn = !isPlayerOneTurn,
            turnTimer = turnTimer,
            isVideoActive = isCallConnected,
            isOnline = true,
            onVideoClick = {
                if (!isCallConnected) {
                    appViewModel.callManager.startOutgoingCall(p2Id, p2Name, CallType.VIDEO)
                } else {
                    appViewModel.callManager.setVideoMinimized(false)
                }
            }
        )

        // Incoming Video Call In-Game Banner
        AnimatedVisibility(
            visible = isCallIncoming,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.5.dp, GutiverseGold, RoundedCornerShape(14.dp))
                    .shadow(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(GutiverseCyan.copy(alpha = 0.2f))
                                .border(1.dp, GutiverseCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = GutiverseCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Incoming Video Call...",
                                style = MaterialTheme.typography.labelSmall.copy(color = GutiverseGold, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${activeCall?.peerName ?: "Opponent"} is calling",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { appViewModel.callManager.rejectOrEndCall() },
                            modifier = Modifier.size(34.dp).clip(CircleShape).background(GutiverseLaserCrimson)
                        ) {
                            Icon(imageVector = Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { appViewModel.callManager.answerCall() },
                            modifier = Modifier.size(34.dp).clip(CircleShape).background(GutiverseSuccessGreen)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Accept", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Multi-Jump Prompt Banner
        AnimatedVisibility(visible = isMultiJumpActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GutiverseLaserCrimson.copy(alpha = 0.18f))
                    .border(1.dp, GutiverseLaserCrimson, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚡ MULTI-JUMP! Jump again or end turn:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseLaserCrimson,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Button(
                        onClick = { gameViewModel.endMultiJumpTurn() },
                        colors = ButtonDefaults.buttonColors(containerColor = GutiverseLaserCrimson),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = appViewModel.getString("end_turn"),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // 2. CENTRAL BOARD AREA WITH IN-GAME LIVE VIDEO OVERLAY
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // 16 Guti Traditional Board Canvas (Always visible and playable during video calls)
            BoardCanvas(
                topology = gameViewModel.engine.topology,
                gameState = gameState,
                onNodeClicked = { nodeId ->
                    gameViewModel.onNodeClicked(nodeId)
                }
            )

            // Live Floating Video Overlay (Draggable, minimizable, live opponent feed + local CameraX preview)
            if (activeCall != null && activeCall?.status == CallStatus.CONNECTED) {
                LiveFloatingVideoOverlay(
                    callSession = activeCall!!,
                    isMinimized = isVideoMinimized,
                    onToggleMinimize = {
                        appViewModel.callManager.setVideoMinimized(!isVideoMinimized)
                    },
                    onMuteToggle = { appViewModel.callManager.toggleMute() },
                    onCameraToggle = { appViewModel.callManager.toggleCamera() },
                    onSwitchCamera = { appViewModel.callManager.switchCamera() },
                    onSpeakerToggle = { appViewModel.callManager.toggleSpeaker() },
                    onEndCall = { appViewModel.callManager.rejectOrEndCall() },
                    onExpandCall = { appViewModel.navigateTo(AppScreen.ACTIVE_CALL) },
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                )
            }
        }

        // 3. BOTTOM SIDE: PLAYER ONE PROFILE PANEL (Rich live profile with photo, username, ID, beads, score, and video status)
        PlayerProfilePanel(
            playerName = p1Name,
            playerId = p1Id,
            beadsAlive = p1BeadsAlive,
            score = gameState.playerOneCaptured,
            beadColor = Color(0xFF00E676),
            avatarResId = R.drawable.gutiverse_icon_1794220208403,
            isTurn = isPlayerOneTurn,
            turnTimer = turnTimer,
            isVideoActive = isCallConnected,
            isOnline = true,
            onVideoClick = {
                if (!isCallConnected) {
                    appViewModel.callManager.startOutgoingCall(p2Id, p2Name, CallType.VIDEO)
                } else {
                    appViewModel.callManager.setVideoMinimized(false)
                }
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Bottom Match Actions (Surrender & Rules)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            Button(
                onClick = { showSurrenderConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = GutiverseSurfaceDark),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFF453868), RoundedCornerShape(10.dp))
            ) {
                Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(appViewModel.getString("surrender"), color = Color.White, fontSize = 11.sp)
            }

            Button(
                onClick = { showRulesDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GutiverseSurfaceDark),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, GutiverseGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = GutiverseGold, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(appViewModel.getString("rules"), color = GutiverseGold, fontSize = 11.sp)
            }
        }
        }
    }

    // Victory / Defeat Modal
    if (gameState.winner != null) {
        val isP1Winner = gameState.winner == PlayerSide.PLAYER_ONE
        AlertDialog(
            onDismissRequest = {},
            containerColor = GutiverseSurfaceElevated,
            title = {
                Text(
                    text = if (isP1Winner) "PLAYER ONE WINS!" else "PLAYER TWO WINS!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = if (isP1Winner) Color(0xFF00E676) else Color(0xFFFF9100),
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isP1Winner) {
                            "Player One captured all opposing orange beads and conquered the 16 Guti Arena!"
                        } else {
                            "Player Two captured all opposing green beads and achieved victory!"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GutiverseSurfaceDark)
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Moves", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                            Text("${gameState.moveHistoryCount}", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("P1 Captures", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                            Text("${gameState.playerOneCaptured}", style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF00E676), fontWeight = FontWeight.Bold))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("P2 Captures", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                            Text("${gameState.playerTwoCaptured}", style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFFF9100), fontWeight = FontWeight.Bold))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { gameViewModel.restartMatch() },
                    colors = ButtonDefaults.buttonColors(containerColor = GutiverseGold)
                ) {
                    Text(appViewModel.getString("rematch"), color = GutiverseDarkVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { appViewModel.navigateTo(AppScreen.HOME) }
                ) {
                    Text(appViewModel.getString("back_to_menu"), color = Color.White)
                }
            }
        )
    }

    // Rules & Info Dialog
    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            containerColor = GutiverseSurfaceElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = GutiverseGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "16 Guti (Sholo Guti) Rules",
                        style = MaterialTheme.typography.titleMedium.copy(color = GutiverseGold, fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) {
                            RulesConfiguration.DOCUMENTED_RULES_BN
                        } else {
                            RulesConfiguration.DOCUMENTED_RULES_EN
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFE2DCF0),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRulesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GutiverseCyan)
                ) {
                    Text("Close", color = GutiverseDarkVoid, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Surrender Confirmation Dialog
    if (showSurrenderConfirm) {
        AlertDialog(
            onDismissRequest = { showSurrenderConfirm = false },
            containerColor = GutiverseSurfaceElevated,
            title = {
                Text("Confirm Surrender?", color = GutiverseLaserCrimson, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to surrender this match to your opponent?", color = Color.White)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSurrenderConfirm = false
                        appViewModel.navigateTo(AppScreen.HOME)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GutiverseLaserCrimson)
                ) {
                    Text("Yes, Surrender", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSurrenderConfirm = false }) {
                    Text("Keep Playing", color = Color.Gray)
                }
            }
        )
    }
}

/**
 * Reusable, compact, responsive live player profile panel.
 * Displays photo, username, ID, score, remaining beads, online status, and video indicator.
 */
@Composable
private fun PlayerProfilePanel(
    playerName: String,
    playerId: String,
    beadsAlive: Int,
    score: Int,
    beadColor: Color,
    avatarResId: Int,
    isTurn: Boolean,
    turnTimer: Int,
    isVideoActive: Boolean,
    isOnline: Boolean,
    onVideoClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isTurn) Color(0xFF1F183E) else Color(0xFF16122C)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isTurn) 2.dp else 1.dp,
                color = if (isTurn) beadColor else Color(0xFF382F5A),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Left: Circular Avatar with Online Badge + Player Name & ID
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(42.dp)) {
                    // Profile Photo
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .border(1.8.dp, beadColor, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = avatarResId),
                            contentDescription = playerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Green Glowing Online Indicator Dot
                    if (isOnline) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(GutiverseSuccessGreen)
                                .border(1.dp, Color(0xFF16122C), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = playerName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // Bead Color Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(beadColor)
                        )
                    }
                    Text(
                        text = "ID: $playerId • ONLINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Center: Score & Beads Counter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GutiverseGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = "Beads: $beadsAlive/16",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFD1C8E8),
                        fontSize = 10.sp
                    )
                )
            }

            // Right: Video Call Indicator & Turn Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Video Call Indicator Chip / Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isVideoActive) GutiverseSuccessGreen.copy(alpha = 0.2f)
                            else Color(0xFF231A45)
                        )
                        .border(
                            1.dp,
                            if (isVideoActive) GutiverseSuccessGreen else Color(0xFF4C3E75),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onVideoClick() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video",
                            tint = if (isVideoActive) GutiverseSuccessGreen else GutiverseCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isVideoActive) "LIVE" else "VIDEO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isVideoActive) GutiverseSuccessGreen else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                if (isTurn) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(beadColor.copy(alpha = 0.2f))
                            .border(1.dp, beadColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${turnTimer}s",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = beadColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
