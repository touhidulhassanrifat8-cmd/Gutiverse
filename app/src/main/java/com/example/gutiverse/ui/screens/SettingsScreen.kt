package com.example.gutiverse.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gutiverse.localization.AppLanguage
import com.example.ui.theme.GutiverseCyan
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseGold
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel

@Composable
fun SettingsScreen(
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val language by appViewModel.language.collectAsState()
    val rulesConfig by appViewModel.rulesConfig.collectAsState()

    var musicOn by remember { mutableStateOf(appViewModel.soundManager.isMusicEnabled) }
    var sfxOn by remember { mutableStateOf(appViewModel.soundManager.isSfxEnabled) }
    var voiceOn by remember { mutableStateOf(appViewModel.soundManager.isVoiceEnabled) }
    var masterVol by remember { mutableStateOf(appViewModel.soundManager.masterVolume) }

    var multiJumpOn by remember { mutableStateOf(rulesConfig.allowMultiJump) }
    var mandatoryCaptureOn by remember { mutableStateOf(rulesConfig.mandatoryCapture) }

    BackHandler {
        appViewModel.navigateTo(AppScreen.HOME)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { appViewModel.navigateTo(AppScreen.HOME) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = appViewModel.getString("menu_settings"),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = GutiverseGold,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 1: Multilingual Selector (11 Languages)
        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GutiverseCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = GutiverseCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appViewModel.getString("language_select"),
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Language Grid / Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppLanguage.entries.chunked(3).forEach { rowLangs ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowLangs.forEach { lang ->
                                val isSelected = language == lang
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) GutiverseCyan else GutiverseSurfaceElevated)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color.White else Color(0xFF3B2D5E),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            appViewModel.soundManager.playButtonClick()
                                            appViewModel.setLanguage(lang)
                                        }
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = lang.nativeName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) GutiverseDarkVoid else Color.White,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 2: Robotic Audio & Voice Engine
        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GutiverseGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = GutiverseGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = appViewModel.getString("audio_settings"),
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Master Volume Slider
                Text(
                    text = "Master Volume: ${(masterVol * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                )
                Slider(
                    value = masterVol,
                    onValueChange = {
                        masterVol = it
                        appViewModel.soundManager.masterVolume = it
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = GutiverseGold,
                        activeTrackColor = GutiverseGold,
                        inactiveTrackColor = Color(0xFF3B2D5E)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // BGM Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = GutiverseCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(appViewModel.getString("music_toggle"), color = Color.White, fontSize = 13.sp)
                    }
                    Switch(
                        checked = musicOn,
                        onCheckedChange = {
                            musicOn = it
                            appViewModel.soundManager.isMusicEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GutiverseCyan, checkedTrackColor = GutiverseSurfaceElevated)
                    )
                }

                // SFX Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = GutiverseGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(appViewModel.getString("sfx_toggle"), color = Color.White, fontSize = 13.sp)
                    }
                    Switch(
                        checked = sfxOn,
                        onCheckedChange = {
                            sfxOn = it
                            appViewModel.soundManager.isSfxEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GutiverseGold, checkedTrackColor = GutiverseSurfaceElevated)
                    )
                }

                // Robot Voice Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFF69F0AE), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(appViewModel.getString("voice_toggle"), color = Color.White, fontSize = 13.sp)
                    }
                    Switch(
                        checked = voiceOn,
                        onCheckedChange = {
                            voiceOn = it
                            appViewModel.soundManager.isVoiceEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF69F0AE), checkedTrackColor = GutiverseSurfaceElevated)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 3: Configurable Rules Engine
        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF64B5F6).copy(alpha = 0.35f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Rule, contentDescription = null, tint = Color(0xFF64B5F6))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rules Engine Configuration",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(appViewModel.getString("multi_jump_rule"), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Allow chaining multiple jump captures in a single turn", color = Color.Gray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = multiJumpOn,
                        onCheckedChange = {
                            multiJumpOn = it
                            appViewModel.updateRules(rulesConfig.copy(allowMultiJump = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF64B5F6), checkedTrackColor = GutiverseSurfaceElevated)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mandatory Capture (Forced)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Requires a capture move if one is available on board", color = Color.Gray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = mandatoryCaptureOn,
                        onCheckedChange = {
                            mandatoryCaptureOn = it
                            appViewModel.updateRules(rulesConfig.copy(mandatoryCapture = it))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF64B5F6), checkedTrackColor = GutiverseSurfaceElevated)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 4: About & Creator Credit
        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GutiverseGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "GUTIVERSE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GutiverseGold,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    text = "16 BEAD MULTIPLAYER ARENA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GutiverseCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tagline: PLAY • CONNECT • CONQUER",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Created by Touhidul Hassan Rifat",
                    style = MaterialTheme.typography.bodySmall.copy(color = GutiverseGold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Version 1.0.0 (Phase 1 Build) • Native Kotlin & Jetpack Compose",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
            }
        }
    }
}
