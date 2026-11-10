package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.gutiverse.ui.screens.CinematicIntroScreen
import com.example.gutiverse.ui.screens.FriendsScreen
import com.example.gutiverse.ui.screens.GameScreen
import com.example.gutiverse.ui.screens.GameShopScreen
import com.example.gutiverse.ui.screens.HomeScreen
import com.example.gutiverse.ui.screens.LeaderboardScreen
import com.example.gutiverse.ui.screens.MessagesScreen
import com.example.gutiverse.ui.screens.NotificationsScreen
import com.example.gutiverse.ui.screens.OnlineLobbyScreen
import com.example.gutiverse.ui.screens.ProfileScreen
import com.example.gutiverse.ui.screens.SettingsScreen
import com.example.ui.theme.GutiverseDarkVoid
import com.example.ui.theme.GutiverseTheme
import com.example.gutiverse.viewmodel.AppScreen
import com.example.gutiverse.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentScreen by appViewModel.currentScreen.collectAsState()
            val language by appViewModel.language.collectAsState()

            val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                GutiverseTheme {
                    Surface(
                        color = GutiverseDarkVoid,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                            when (screen) {
                                AppScreen.CINEMATIC_INTRO -> CinematicIntroScreen(appViewModel)
                                AppScreen.HOME -> HomeScreen(appViewModel)
                                AppScreen.GAME -> GameScreen(appViewModel)
                                AppScreen.PROFILE -> ProfileScreen(appViewModel)
                                AppScreen.SHOP -> GameShopScreen(appViewModel)
                                AppScreen.LEADERBOARD -> LeaderboardScreen(appViewModel)
                                AppScreen.FRIENDS -> FriendsScreen(appViewModel)
                                AppScreen.ONLINE_LOBBY -> OnlineLobbyScreen(appViewModel)
                                AppScreen.MESSAGES -> MessagesScreen(appViewModel)
                                AppScreen.NOTIFICATIONS -> NotificationsScreen(appViewModel)
                                AppScreen.SETTINGS -> SettingsScreen(appViewModel)
                                AppScreen.PRIVATE_CHAT -> com.example.gutiverse.ui.screens.PrivateChatScreen(appViewModel, appViewModel.activeChatPeerId)
                                AppScreen.ACTIVE_CALL -> com.example.gutiverse.ui.screens.ActiveCallScreen(appViewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appViewModel.soundManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        appViewModel.soundManager.onPause()
    }
}
