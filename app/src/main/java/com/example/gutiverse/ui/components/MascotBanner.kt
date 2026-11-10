package com.example.gutiverse.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseSurfaceElevated

@Composable
fun MascotBanner(
    speechText: String,
    onSpeakClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GutiverseSurfaceElevated.copy(alpha = 0.90f))
            .border(1.dp, GutiverseCyan.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // GutiBot Mascot Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(2.dp, GutiverseCyan, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gutibot_mascot_1794220235371),
                    contentDescription = "GutiBot Mascot",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Speech Text
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "GutiBot AI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ARENA COMPANION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseCyan.copy(alpha = 0.8f),
                            fontSize = 9.sp
                        )
                    )
                }
                Text(
                    text = speechText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Replay Voice Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GutiverseCyan.copy(alpha = 0.15f))
                    .border(1.dp, GutiverseCyan.copy(alpha = 0.6f), CircleShape)
                    .clickable { onSpeakClicked() }
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speak line",
                    tint = GutiverseCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
