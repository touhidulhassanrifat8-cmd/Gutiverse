package com.example.gutiverse.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.gutiverse.communication.models.CallSession
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseLaserCrimson
import com.example.ui.theme.GutiverseSuccessGreen
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import kotlin.math.roundToInt

@Composable
fun LiveFloatingVideoOverlay(
    callSession: CallSession,
    isMinimized: Boolean,
    onToggleMinimize: () -> Unit,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onSwitchCamera: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onEndCall: () -> Unit,
    onExpandCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Draggable position offsets
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val durationMin = callSession.durationSeconds / 60
    val durationSec = callSession.durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", durationMin, durationSec)

    val pulseTransition = rememberInfiniteTransition(label = "remoteAudioPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    if (isMinimized) {
        // --- MINIMIZED COMPACT FLOATING CHIP ---
        Box(
            modifier = modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(GutiverseSurfaceElevated.copy(alpha = 0.96f))
                .border(1.5.dp, GutiverseCyan, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Restore button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleMinimize() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GutiverseSuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📹 ${callSession.peerName} ($formattedDuration)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Expand",
                        tint = GutiverseCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Quick Mute Mic
                IconButton(
                    onClick = onMuteToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (callSession.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = if (callSession.isMuted) GutiverseLaserCrimson else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // End call
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(GutiverseLaserCrimson)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    } else {
        // --- EXPANDED FLOATING VIDEO WINDOW (MOVABLE ANYWHERE ON SCREEN) ---
        Box(
            modifier = modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    // Consume taps so game board is never triggered by accident
                    detectTapGestures { }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .size(width = 240.dp, height = 290.dp)
                .shadow(16.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF130E26))
                .border(2.dp, Brush.horizontalGradient(listOf(GutiverseCyan, GutiverseGold)), RoundedCornerShape(22.dp))
        ) {
            // Main Opponent Video Stream Feed Representation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF201642), Color(0xFF0F0A21))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Simulated Opponent Remote Camera Feed with Live Audio Waveform Glow
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(86.dp)
                    ) {
                        // Pulsing LiveKit audio indicator ring
                        Box(
                            modifier = Modifier
                                .size(86.dp * pulseAlpha)
                                .clip(CircleShape)
                                .background(GutiverseCyan.copy(alpha = 0.25f))
                        )
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, GutiverseGold, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                                contentDescription = callSession.peerName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = callSession.peerName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "● LiveKit Remote Video Feed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseSuccessGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // PiP: Local User Camera Feed (Top Right of Floating Window)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(width = 72.dp, height = 96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090414))
                        .border(1.5.dp, GutiverseCyan, RoundedCornerShape(12.dp))
                ) {
                    CameraPreview(
                        isFrontCamera = callSession.isFrontCamera,
                        isCameraOn = callSession.isCameraOn,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "You",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // Drag Handle & Top Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Drag Window",
                            tint = GutiverseGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDuration,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GutiverseGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Row {
                        // Minimize to chip
                        IconButton(
                            onClick = onToggleMinimize,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Minimize",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Fullscreen
                        IconButton(
                            onClick = onExpandCall,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Bottom Video Control Bar
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFF090414).copy(alpha = 0.88f))
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    // Mic Mute
                    IconButton(
                        onClick = onMuteToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (callSession.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mic",
                            tint = if (callSession.isMuted) GutiverseLaserCrimson else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Camera On/Off
                    IconButton(
                        onClick = onCameraToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (callSession.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Camera",
                            tint = if (callSession.isCameraOn) GutiverseCyan else GutiverseLaserCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Switch Front / Back Camera
                    IconButton(
                        onClick = onSwitchCamera,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = GutiverseGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Speakerphone
                    IconButton(
                        onClick = onSpeakerToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = if (callSession.isSpeakerOn) GutiverseCyan else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // End Call
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(GutiverseLaserCrimson)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
