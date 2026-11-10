package com.example.gutiverse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel

@Composable
fun CinematicIntroScreen(
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isReady = true
        // Trigger welcome voice greeting from GutiBot
        appViewModel.mascotVoiceManager.speakWelcome()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
    ) {
        // Hero Cinematic Esports Arena Background
        Image(
            painter = painterResource(id = R.drawable.esports_arena_hero_1794220222921),
            contentDescription = "Esports Arena",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Dark Overlays for Readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xEE080414),
                            Color(0x66080414),
                            Color(0xEE080414),
                            GutiverseDarkVoid
                        )
                    )
                )
        )

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            // Header: Creator Credit
            AnimatedVisibility(
                visible = isReady,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GutiverseSurfaceDark.copy(alpha = 0.85f))
                            .border(1.dp, GutiverseGold.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Created by Touhidul Hassan Rifat",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GutiverseGold,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            // Middle: Branding and Mascot Greeting
            AnimatedVisibility(
                visible = isReady,
                enter = fadeIn(tween(1000))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Glowing Title
                    Text(
                        text = "GUTIVERSE",
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = GutiverseGold,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 4.sp
                        )
                    )

                    Text(
                        text = "16 BEAD MULTIPLAYER ARENA",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = GutiverseCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )

                    Text(
                        text = "PLAY • CONNECT • CONQUER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFAFA7C9),
                            letterSpacing = 3.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Mascot Greeting Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(GutiverseSurfaceElevated.copy(alpha = 0.92f))
                            .border(1.5.dp, GutiverseCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // GutiBot Portrait
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, GutiverseCyan, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.gutibot_mascot_1794220235371),
                                    contentDescription = "GutiBot Mascot",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "GutiBot AI Mascot",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = GutiverseGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Replay Greeting",
                                        tint = GutiverseCyan,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                appViewModel.mascotVoiceManager.speakWelcome()
                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "“Assalamu Alaikum wa Rahmatullahi wa Barakatuh! Welcome to GUTIVERSE! Enter the ultimate 16 Bead gaming arena. Are you ready to play?”",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Actions: Enter Arena
            AnimatedVisibility(
                visible = isReady,
                enter = fadeIn(tween(1200)) + slideInVertically(tween(1200))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            appViewModel.soundManager.playButtonClick()
                            appViewModel.navigateTo(AppScreen.HOME)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GutiverseGold,
                            contentColor = GutiverseDarkVoid
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(54.dp)
                            .border(1.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = appViewModel.getString("enter_arena"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Touch to launch your journey in Sholo Guti Esports",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF70678E),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
