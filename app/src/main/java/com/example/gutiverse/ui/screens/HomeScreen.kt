package com.example.gutiverse.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.gutiverse.engine.BotDifficulty
import com.example.gutiverse.ui.components.MascotBanner
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseLaserCrimson
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel
import com.example.gutiverse.viewmodel.GameMode

data class MenuItem(
    val titleKey: String,
    val icon: ImageVector,
    val color: Color,
    val badge: String,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by appViewModel.language.collectAsState()
    val coins by appViewModel.playerCoins.collectAsState()
    val level by appViewModel.playerLevel.collectAsState()

    var showDifficultyDialog by remember { mutableStateOf(false) }
    var selectedDifficultyMode by remember { mutableStateOf(GameMode.ROBOT) }

    val menuItems = listOf(
        MenuItem(
            titleKey = "mode_16_guti",
            icon = Icons.Default.SportsEsports,
            color = GutiverseGold,
            badge = "16 vs 16",
            onClick = {
                selectedDifficultyMode = GameMode.SHOLO_GUTI_CLASSIC
                showDifficultyDialog = true
            }
        ),
        MenuItem(
            titleKey = "menu_robot_mode",
            icon = Icons.Default.SmartToy,
            color = GutiverseCyan,
            badge = "AI Mascot",
            onClick = {
                selectedDifficultyMode = GameMode.ROBOT
                showDifficultyDialog = true
            }
        ),
        MenuItem(
            titleKey = "menu_vs_computer",
            icon = Icons.Default.Computer,
            color = Color(0xFF64B5F6),
            badge = "Custom",
            onClick = {
                selectedDifficultyMode = GameMode.VS_COMPUTER
                showDifficultyDialog = true
            }
        ),
        MenuItem(
            titleKey = "menu_play_online",
            icon = Icons.Default.Public,
            color = GutiverseGold,
            badge = "Live Arena",
            onClick = {
                appViewModel.navigateTo(AppScreen.ONLINE_LOBBY)
            }
        ),
        MenuItem(
            titleKey = "menu_local_game",
            icon = Icons.Default.SportsEsports,
            color = Color(0xFF69F0AE),
            badge = "2 Players",
            onClick = {
                appViewModel.startMatch(GameMode.LOCAL_2P)
            }
        ),
        MenuItem(
            titleKey = "menu_friends",
            icon = Icons.Default.Group,
            color = Color(0xFFFF80AB),
            badge = "Social",
            onClick = {
                appViewModel.navigateTo(AppScreen.FRIENDS)
            }
        ),
        MenuItem(
            titleKey = "menu_leaderboard",
            icon = Icons.Default.EmojiEvents,
            color = GutiverseGold,
            badge = "Top 100",
            onClick = {
                appViewModel.navigateTo(AppScreen.LEADERBOARD)
            }
        ),
        MenuItem(
            titleKey = "menu_my_profile",
            icon = Icons.Default.Person,
            color = Color(0xFFB388FF),
            badge = "Stats",
            onClick = {
                appViewModel.navigateTo(AppScreen.PROFILE)
            }
        ),
        MenuItem(
            titleKey = "menu_messages",
            icon = Icons.Default.Mail,
            color = Color(0xFF4DD0E1),
            badge = "Inbox",
            onClick = {
                appViewModel.navigateTo(AppScreen.MESSAGES)
            }
        ),
        MenuItem(
            titleKey = "menu_notifications",
            icon = Icons.Default.Notifications,
            color = Color(0xFFFFB74D),
            badge = "News",
            onClick = {
                appViewModel.navigateTo(AppScreen.NOTIFICATIONS)
            }
        ),
        MenuItem(
            titleKey = "menu_game_shop",
            icon = Icons.Default.ShoppingBag,
            color = Color(0xFFFFD54F),
            badge = "Shop",
            onClick = {
                appViewModel.navigateTo(AppScreen.SHOP)
            }
        ),
        MenuItem(
            titleKey = "menu_settings",
            icon = Icons.Default.Settings,
            color = Color(0xFF90A4AE),
            badge = "Audio & Lang",
            onClick = {
                appViewModel.navigateTo(AppScreen.SETTINGS)
            }
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Player Status Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GutiverseSurfaceDark)
                .border(1.dp, Color(0xFF332555), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Player Profile Brief
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { appViewModel.navigateTo(AppScreen.PROFILE) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GutiverseGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Touhidul_Rifat",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "LEVEL $level • PRO ARENA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Quick Actions: Coins & Music Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Coins Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GutiverseSurfaceElevated)
                        .border(1.dp, GutiverseGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "🪙 $coins",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GutiverseGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Music Toggle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GutiverseSurfaceElevated)
                        .clickable {
                            appViewModel.soundManager.isMusicEnabled = !appViewModel.soundManager.isMusicEnabled
                        }
                ) {
                    Icon(
                        imageVector = if (appViewModel.soundManager.isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                        contentDescription = "Music Toggle",
                        tint = if (appViewModel.soundManager.isMusicEnabled) GutiverseCyan else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // GutiBot Mascot Banner
        MascotBanner(
            speechText = "Welcome to the arena! Choose Robot Mode to test your skills against my AI heuristics!",
            onSpeakClicked = {
                appViewModel.mascotVoiceManager.speak("Welcome to the arena! Choose Robot Mode to test your skills against my AI heuristics!")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Featured 16 Guti (Sholo Guti) Arena Card
        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, GutiverseGold.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⭐ FEATURED MODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseGold,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "16 vs 16 BEADS (32 TOTAL)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GutiverseCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = appViewModel.getString("mode_16_guti"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Text(
                    text = appViewModel.getString("beads_count_info"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFAFA7C9),
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            appViewModel.soundManager.playButtonClick()
                            selectedDifficultyMode = GameMode.SHOLO_GUTI_CLASSIC
                            showDifficultyDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GutiverseGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text("Play vs AI Robot", color = GutiverseDarkVoid, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            appViewModel.soundManager.playButtonClick()
                            appViewModel.startMatch(GameMode.LOCAL_2P)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GutiverseCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text("Local 2-Player", color = GutiverseDarkVoid, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section Title: Game Modes & Hubs
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            Text(
                text = "ESPORTS ARENA HUBS",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = GutiverseCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            )
            Text(
                text = "11 DESTINATIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF70678E)
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 11 Core Destinations Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(menuItems.size) { index ->
                val item = menuItems[index]
                Card(
                    onClick = {
                        appViewModel.soundManager.playButtonClick()
                        item.onClick()
                    },
                    colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .border(1.dp, item.color.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(item.color.copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = item.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    text = item.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = item.color,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(item.color.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = appViewModel.getString(item.titleKey),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Creator Credit Footer
        Text(
            text = "GUTIVERSE • Created by Touhidul Hassan Rifat • PLAY • CONNECT • CONQUER",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF6B5F8D),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp)
        )
    }

    // Difficulty Selection Dialog for Robot & Vs Computer
    if (showDifficultyDialog) {
        AlertDialog(
            onDismissRequest = { showDifficultyDialog = false },
            containerColor = GutiverseSurfaceElevated,
            title = {
                Text(
                    text = if (selectedDifficultyMode == GameMode.ROBOT) "Select Robot Difficulty" else "Select Computer Level",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GutiverseGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose your AI opponent's tactical intelligence level:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFAFA7C9))
                    )

                    val difficulties = listOf(
                        Triple(BotDifficulty.EASY, "Easy", "Casual training, occasional captures"),
                        Triple(BotDifficulty.MEDIUM, "Medium", "Greedy tactical strikes, center focus"),
                        Triple(BotDifficulty.HARD, "Hard", "Alpha-Beta Minimax search, deep defense")
                    )

                    difficulties.forEach { (diff, label, desc) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GutiverseSurfaceDark)
                                .border(1.dp, GutiverseCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable {
                                    showDifficultyDialog = false
                                    appViewModel.startMatch(selectedDifficultyMode, diff)
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = GutiverseCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDifficultyDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
