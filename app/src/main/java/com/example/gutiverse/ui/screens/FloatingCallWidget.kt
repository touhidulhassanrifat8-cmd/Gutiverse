package com.example.gutiverse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gutiverse.communication.models.CallSession
import com.example.gutiverse.communication.models.CallType
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseLaserCrimson
import com.example.ui.theme.GutiverseSurfaceElevated

@Composable
fun FloatingCallWidget(
    callSession: CallSession,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onEndCall: () -> Unit,
    onExpandCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMin = callSession.durationSeconds / 60
    val durationSec = callSession.durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", durationMin, durationSec)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GutiverseSurfaceElevated.copy(alpha = 0.95f))
            .border(1.5.dp, if (callSession.callType == CallType.VIDEO) GutiverseCyan else GutiverseGold, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onExpandCall() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Call status dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (callSession.callType == CallType.VIDEO) GutiverseCyan else GutiverseGold)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "${callSession.peerName} (${if (callSession.callType == CallType.VIDEO) "Video" else "Audio"})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "Call Active: $formattedDuration • Tap to open",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseCyan,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mute Mic Button
                IconButton(
                    onClick = onMuteToggle,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (callSession.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = if (callSession.isMuted) GutiverseLaserCrimson else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Camera Toggle if Video Call
                if (callSession.callType == CallType.VIDEO) {
                    IconButton(
                        onClick = onCameraToggle,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (callSession.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Camera",
                            tint = if (callSession.isCameraOn) GutiverseCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Fullscreen expand
                IconButton(
                    onClick = onExpandCall,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Expand Call",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // End Call button
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(32.dp)
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
