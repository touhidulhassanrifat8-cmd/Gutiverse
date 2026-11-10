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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseLaserCrimson
import com.example.ui.theme.GutiverseSuccessGreen
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel

@Composable
fun ActiveCallScreen(
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val callSession by appViewModel.callManager.currentCall.collectAsState()

    // Back button minimizes to game or home
    BackHandler {
        appViewModel.navigateTo(AppScreen.HOME)
    }

    if (callSession == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize().background(GutiverseDarkVoid)
        ) {
            Text("No active call", color = Color.White)
        }
        return
    }

    val call = callSession!!
    val isVideo = call.callType == CallType.VIDEO
    val durationMin = call.durationSeconds / 60
    val durationSec = call.durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", durationMin, durationSec)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Video Call Background / Peer Feed Representation
        if (isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF140833), Color(0xFF070216), Color(0xFF0F0826))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Futuristic peer video frame representation
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(3.dp, GutiverseCyan, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                            contentDescription = call.peerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "${call.peerName} (Live Video Feed)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = call.connectionQuality,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseSuccessGreen,
                            fontSize = 11.sp
                        )
                    )
                }

                // PiP Local Camera Preview (Top Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(110.dp, 160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GutiverseSurfaceElevated)
                        .border(1.5.dp, GutiverseGold, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (call.isCameraOn) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize().padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = GutiverseGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (call.isFrontCamera) "Front Cam (Self)" else "Rear Cam",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    } else {
                        Text(
                            text = "Camera Off",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        } else {
            // Audio Call Screen Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(3.dp, GutiverseGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                        contentDescription = call.peerName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = call.peerName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (call.status) {
                        CallStatus.DIALING -> "Dialing internet call..."
                        CallStatus.INCOMING -> "Incoming Audio Call..."
                        CallStatus.CONNECTED -> "In Call: $formattedDuration"
                        else -> "Connecting..."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (call.status == CallStatus.CONNECTED) GutiverseCyan else GutiverseGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = call.connectionQuality,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GutiverseSuccessGreen,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Top Bar: Back / Minimize to Game Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    // Minimize call and return to 16 Guti Game!
                    appViewModel.soundManager.playButtonClick()
                    appViewModel.navigateTo(AppScreen.GAME)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GutiverseSurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.border(1.dp, GutiverseCyan, RoundedCornerShape(12.dp))
            ) {
                Icon(imageVector = Icons.Default.Gamepad, contentDescription = null, tint = GutiverseCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Play 16 Guti While Calling", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Duration badge in header
            if (call.status == CallStatus.CONNECTED) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = formattedDuration,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GutiverseGold,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Bottom Call Control Deck
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            if (call.status == CallStatus.INCOMING) {
                // Answer or Reject Bar
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
                ) {
                    IconButton(
                        onClick = { appViewModel.callManager.rejectOrEndCall() },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GutiverseLaserCrimson)
                    ) {
                        Icon(imageVector = Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    IconButton(
                        onClick = { appViewModel.callManager.answerCall() },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GutiverseSuccessGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Answer", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            } else {
                // Connected / In-call controls
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(GutiverseSurfaceElevated.copy(alpha = 0.95f))
                        .border(1.dp, Color(0xFF3B2C63), RoundedCornerShape(24.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    // Mute Microphone
                    IconButton(
                        onClick = { appViewModel.callManager.toggleMute() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (call.isMuted) GutiverseLaserCrimson.copy(alpha = 0.3f) else GutiverseSurfaceDark)
                    ) {
                        Icon(
                            imageVector = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (call.isMuted) GutiverseLaserCrimson else Color.White
                        )
                    }

                    // Speakerphone toggle
                    IconButton(
                        onClick = { appViewModel.callManager.toggleSpeaker() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (call.isSpeakerOn) GutiverseCyan.copy(alpha = 0.2f) else GutiverseSurfaceDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = if (call.isSpeakerOn) GutiverseCyan else Color.White
                        )
                    }

                    // Video Specific Controls
                    if (isVideo) {
                        // Camera On/Off
                        IconButton(
                            onClick = { appViewModel.callManager.toggleCamera() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (!call.isCameraOn) GutiverseLaserCrimson.copy(alpha = 0.3f) else GutiverseSurfaceDark)
                        ) {
                            Icon(
                                imageVector = if (call.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Camera Toggle",
                                tint = if (call.isCameraOn) Color.White else GutiverseLaserCrimson
                            )
                        }

                        // Switch Camera
                        IconButton(
                            onClick = { appViewModel.callManager.switchCamera() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GutiverseSurfaceDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch Camera",
                                tint = GutiverseGold
                            )
                        }
                    }

                    // End Call
                    IconButton(
                        onClick = {
                            appViewModel.callManager.rejectOrEndCall()
                            appViewModel.navigateTo(AppScreen.HOME)
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(GutiverseLaserCrimson)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
