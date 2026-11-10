package com.example.gutiverse.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.GutiverseLaserCrimson
import com.example.ui.theme.GutiverseSuccessGreen
import com.example.ui.theme.GutiverseSurfaceDark
import com.example.ui.theme.GutiverseSurfaceElevated
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel

@Composable
fun ProfileScreen(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    BackHandler { appViewModel.navigateTo(AppScreen.HOME) }

    val name by appViewModel.playerName.collectAsState()
    val id by appViewModel.playerId.collectAsState()
    val level by appViewModel.playerLevel.collectAsState()
    val coins by appViewModel.playerCoins.collectAsState()
    val matches by appViewModel.totalMatches.collectAsState()
    val wins by appViewModel.wins.collectAsState()
    val losses by appViewModel.losses.collectAsState()

    val winRate = if (matches > 0) (wins.toFloat() / matches * 100).toInt() else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        HubHeader(title = appViewModel.getString("menu_my_profile"), onBack = { appViewModel.navigateTo(AppScreen.HOME) })

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Identity Card
        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GutiverseGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, GutiverseGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(name, style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                Text("ID: $id • LEVEL $level MASTER", style = MaterialTheme.typography.labelSmall.copy(color = GutiverseCyan, letterSpacing = 1.sp))

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GutiverseSurfaceElevated)
                        .padding(12.dp)
                ) {
                    StatBox("Matches", "$matches", GutiverseCyan)
                    StatBox("Wins", "$wins", GutiverseSuccessGreen)
                    StatBox("Losses", "$losses", GutiverseLaserCrimson)
                    StatBox("Win Rate", "$winRate%", GutiverseGold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("BADGES & ACHIEVEMENTS", style = MaterialTheme.typography.labelMedium.copy(color = GutiverseCyan, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            BadgeCard("🏆 Arena Champion", "Win 30 competitive matches", true, Modifier.weight(1f))
            BadgeCard("⚡ Multi-Striker", "Chain 3 jump captures in 1 turn", true, Modifier.weight(1f))
        }
    }
}

@Composable
fun LeaderboardScreen(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    BackHandler { appViewModel.navigateTo(AppScreen.HOME) }

    val topPlayers = listOf(
        Triple("1. Touhidul_Rifat", "3,420 ELO", "👑 Grandmaster"),
        Triple("2. CyberGuti_99", "3,190 ELO", "⭐ Master"),
        Triple("3. DhakaStriker", "3,050 ELO", "⭐ Master"),
        Triple("4. NeonWarrior", "2,840 ELO", "💎 Diamond"),
        Triple("5. SultanOf16", "2,710 ELO", "💎 Diamond")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        HubHeader(title = appViewModel.getString("menu_leaderboard"), onBack = { appViewModel.navigateTo(AppScreen.HOME) })
        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(topPlayers.size) { i ->
                val (pName, elo, tier) = topPlayers[i]
                Card(
                    colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (i == 0) GutiverseGold else Color(0xFF332555), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(14.dp)
                    ) {
                        Column {
                            Text(pName, style = MaterialTheme.typography.titleSmall.copy(color = if (i == 0) GutiverseGold else Color.White, fontWeight = FontWeight.Bold))
                            Text(tier, style = MaterialTheme.typography.labelSmall.copy(color = GutiverseCyan, fontSize = 10.sp))
                        }
                        Text(elo, style = MaterialTheme.typography.titleSmall.copy(color = GutiverseGold, fontWeight = FontWeight.ExtraBold))
                    }
                }
            }
        }
    }
}

@Composable
fun GameShopScreen(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    BackHandler { appViewModel.navigateTo(AppScreen.HOME) }
    val coins by appViewModel.playerCoins.collectAsState()

    val shopItems = listOf(
        Triple("Imperial Gold Beads", "500 Coins", "ACTIVE"),
        Triple("Holographic Cyan Core", "750 Coins", "PURCHASE"),
        Triple("Cyber Purple Arena Skin", "1,000 Coins", "PURCHASE"),
        Triple("Neon Visor Frame", "300 Coins", "PURCHASE")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        HubHeader(title = appViewModel.getString("menu_game_shop"), onBack = { appViewModel.navigateTo(AppScreen.HOME) })
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(GutiverseSurfaceElevated)
                .border(1.dp, GutiverseGold, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Your Balance", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
                Text("🪙 $coins Coins", style = MaterialTheme.typography.titleMedium.copy(color = GutiverseGold, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(shopItems.size) { i ->
                val (itemTitle, price, status) = shopItems[i]
                Card(
                    colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF332555), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(14.dp)
                    ) {
                        Column {
                            Text(itemTitle, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                            Text(price, style = MaterialTheme.typography.labelSmall.copy(color = GutiverseGold, fontSize = 11.sp))
                        }
                        Button(
                            onClick = { appViewModel.soundManager.playButtonClick() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (status == "ACTIVE") GutiverseSuccessGreen else GutiverseCyan
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(status, color = GutiverseDarkVoid, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnlineLobbyScreen(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    BackHandler { appViewModel.navigateTo(AppScreen.HOME) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        HubHeader(title = appViewModel.getString("menu_play_online"), onBack = { appViewModel.navigateTo(AppScreen.HOME) })

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    color = GutiverseCyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(110.dp)
                )
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = GutiverseGold,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = appViewModel.getString("searching_opponent"),
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Global Server: AP-South (Dhaka Gateway) • Ping: 18ms",
                style = MaterialTheme.typography.bodySmall.copy(color = GutiverseCyan, fontSize = 11.sp)
            )
        }

        Button(
            onClick = {
                appViewModel.soundManager.playButtonClick()
                appViewModel.navigateTo(AppScreen.HOME)
            },
            colors = ButtonDefaults.buttonColors(containerColor = GutiverseLaserCrimson),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.85f).height(50.dp)
        ) {
            Text("Cancel Matchmaking", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FriendsScreen(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    BackHandler { appViewModel.navigateTo(AppScreen.HOME) }

    val friends by appViewModel.communicationRepository.friends.collectAsState()
    val blockedIds by appViewModel.communicationRepository.blockedUserIds.collectAsState()
    var searchQuery by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var searchResult by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.gutiverse.communication.models.GutiverseFriend?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        HubHeader(title = appViewModel.getString("menu_friends"), onBack = { appViewModel.navigateTo(AppScreen.HOME) })
        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar for Player ID
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GutiverseSurfaceDark)
                .border(1.dp, Color(0xFF332555), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GutiverseGold)
            Spacer(modifier = Modifier.width(8.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    searchResult = appViewModel.communicationRepository.searchPlayerById(it)
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text("Search by Player ID (e.g. GUTI-1024-DH)...", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                    }
                    innerTextField()
                }
            )
        }

        // Show Search Result prompt if searched
        if (searchResult != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, GutiverseCyan, RoundedCornerShape(12.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    Column {
                        Text(searchResult!!.name, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        Text("ID: ${searchResult!!.id} • Level ${searchResult!!.level}", style = MaterialTheme.typography.labelSmall.copy(color = GutiverseCyan))
                    }
                    Button(
                        onClick = {
                            appViewModel.communicationRepository.sendFriendRequest(searchResult!!.id)
                            appViewModel.soundManager.playButtonClick()
                            searchQuery = ""
                            searchResult = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GutiverseSuccessGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add Friend", color = GutiverseDarkVoid, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "FRIENDS & ALLIES (${friends.size})",
            style = MaterialTheme.typography.labelMedium.copy(color = GutiverseGold, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(friends.size) { i ->
                val f = friends[i]
                val isBlocked = blockedIds.contains(f.id)
                Card(
                    colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (f.isFavorite) GutiverseGold else Color(0xFF332555), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (f.status == com.example.gutiverse.communication.models.UserStatus.ONLINE) GutiverseSuccessGreen else Color.Gray, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                                    contentDescription = f.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(f.name, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                    if (f.isFavorite) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("⭐", fontSize = 11.sp)
                                    }
                                }
                                Text(
                                    text = if (isBlocked) "Blocked" else when (f.status) {
                                        com.example.gutiverse.communication.models.UserStatus.ONLINE -> "Online"
                                        com.example.gutiverse.communication.models.UserStatus.IN_GAME -> "In 16 Guti Match"
                                        com.example.gutiverse.communication.models.UserStatus.OFFLINE -> f.lastSeenText
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (f.status == com.example.gutiverse.communication.models.UserStatus.ONLINE) GutiverseSuccessGreen else Color.Gray,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Action Buttons: Chat, Call, Challenge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    appViewModel.openChat(f.id)
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Mail, contentDescription = "Message", tint = GutiverseCyan, modifier = Modifier.size(20.dp))
                            }

                            IconButton(
                                onClick = {
                                    appViewModel.startCall(f.id, f.name, com.example.gutiverse.communication.models.CallType.AUDIO)
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = GutiverseGold, modifier = Modifier.size(20.dp))
                            }

                            Button(
                                onClick = {
                                    appViewModel.startMatch(com.example.gutiverse.viewmodel.GameMode.LOCAL_2P)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GutiverseSurfaceElevated),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.border(1.dp, Color(0xFF4C3B75), RoundedCornerShape(8.dp))
                            ) {
                                Text("Play", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessagesScreen(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    BackHandler { appViewModel.navigateTo(AppScreen.HOME) }

    val friends by appViewModel.communicationRepository.friends.collectAsState()
    val allMessages by appViewModel.communicationRepository.chatMessages.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        HubHeader(title = appViewModel.getString("menu_messages"), onBack = { appViewModel.navigateTo(AppScreen.HOME) })
        Spacer(modifier = Modifier.height(14.dp))

        // Trigger Incoming Call Simulation Button (for user verification of calling flow)
        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceElevated),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GutiverseCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .clickable {
                    appViewModel.callManager.triggerSimulatedIncomingCall()
                    appViewModel.navigateTo(AppScreen.ACTIVE_CALL)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = GutiverseCyan)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Test Incoming Call Feature", style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        Text("Simulate an incoming video call from Tanvir", style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray, fontSize = 10.sp))
                    }
                }
                Text("TEST ▶", color = GutiverseCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CONVERSATIONS",
            style = MaterialTheme.typography.labelMedium.copy(color = GutiverseGold, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(friends.size) { i ->
                val friend = friends[i]
                val msgs = allMessages[friend.id].orEmpty()
                val lastMsg = msgs.lastOrNull()

                Card(
                    colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF332555), RoundedCornerShape(14.dp))
                        .clickable { appViewModel.openChat(friend.id) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (friend.status == com.example.gutiverse.communication.models.UserStatus.ONLINE) GutiverseSuccessGreen else Color.Gray, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.gutiverse_icon_1794220208403),
                                    contentDescription = friend.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(friend.name, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = lastMsg?.text ?: "No messages yet. Tap to chat!",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray, fontSize = 11.sp)
                                )
                            }
                        }

                        // Right audio/video call quick actions
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    appViewModel.startCall(friend.id, friend.name, com.example.gutiverse.communication.models.CallType.AUDIO)
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = GutiverseGold, modifier = Modifier.size(20.dp))
                            }
                            IconButton(
                                onClick = {
                                    appViewModel.startCall(friend.id, friend.name, com.example.gutiverse.communication.models.CallType.VIDEO)
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = GutiverseCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    BackHandler { appViewModel.navigateTo(AppScreen.HOME) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GutiverseDarkVoid)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        HubHeader(title = appViewModel.getString("menu_notifications"), onBack = { appViewModel.navigateTo(AppScreen.HOME) })
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, GutiverseGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("🎉 Welcome to GUTIVERSE Phase 1!", style = MaterialTheme.typography.titleSmall.copy(color = GutiverseGold, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text("The 16 Bead Multiplayer Arena created by Touhidul Hassan Rifat is now live with full robot AI and local gameplay.", style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
            }
        }
    }
}

// Helpers
@Composable
private fun HubHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge.copy(color = GutiverseGold, fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(color = color, fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp))
    }
}

@Composable
private fun BadgeCard(title: String, desc: String, earned: Boolean, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GutiverseSurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, if (earned) GutiverseGold else Color.Gray, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium.copy(color = if (earned) GutiverseGold else Color.Gray, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray, fontSize = 9.sp))
        }
    }
}
