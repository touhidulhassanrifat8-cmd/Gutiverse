package com.example.gutiverse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gutiverse.audio.MascotVoiceManager
import com.example.gutiverse.audio.SoundManager
import com.example.gutiverse.engine.BotDifficulty
import com.example.gutiverse.engine.RulesConfiguration
import com.example.gutiverse.localization.AppLanguage
import com.example.gutiverse.localization.LocalizationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppScreen {
    CINEMATIC_INTRO,
    HOME,
    GAME,
    PROFILE,
    SHOP,
    LEADERBOARD,
    FRIENDS,
    ONLINE_LOBBY,
    MESSAGES,
    NOTIFICATIONS,
    SETTINGS,
    PRIVATE_CHAT,
    ACTIVE_CALL
}

enum class GameMode {
    SHOLO_GUTI_CLASSIC,
    ROBOT,
    VS_COMPUTER,
    LOCAL_2P
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SoundManager(application)
    val mascotVoiceManager = MascotVoiceManager(application, soundManager)

    // imo-style Communication & Calling Subsystems
    val communicationRepository = com.example.gutiverse.communication.CommunicationRepository()
    val callManager = com.example.gutiverse.communication.CallManager(application, soundManager, communicationRepository)

    var activeChatPeerId: String = "GUTI-1024-DH"

    fun openChat(friendId: String) {
        activeChatPeerId = friendId
        navigateTo(AppScreen.PRIVATE_CHAT)
    }

    fun startCall(peerId: String, peerName: String, type: com.example.gutiverse.communication.models.CallType) {
        callManager.startOutgoingCall(peerId, peerName, type)
        navigateTo(AppScreen.ACTIVE_CALL)
    }

    private val _currentScreen = MutableStateFlow(AppScreen.CINEMATIC_INTRO)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _gameMode = MutableStateFlow(GameMode.ROBOT)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private val _botDifficulty = MutableStateFlow(BotDifficulty.MEDIUM)
    val botDifficulty: StateFlow<BotDifficulty> = _botDifficulty.asStateFlow()

    private val _rulesConfig = MutableStateFlow(RulesConfiguration.DEFAULT)
    val rulesConfig: StateFlow<RulesConfiguration> = _rulesConfig.asStateFlow()

    // Player Stats
    val playerCoins = MutableStateFlow(1250)
    val playerLevel = MutableStateFlow(7)
    val playerName = MutableStateFlow("Touhidul_Rifat")
    val playerId = MutableStateFlow("GUTI-7894-BD")
    val totalMatches = MutableStateFlow(48)
    val wins = MutableStateFlow(34)
    val losses = MutableStateFlow(14)

    fun navigateTo(screen: AppScreen) {
        soundManager.playMenuTransition()
        _currentScreen.value = screen
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun startMatch(mode: GameMode, difficulty: BotDifficulty = BotDifficulty.MEDIUM) {
        _gameMode.value = mode
        _botDifficulty.value = difficulty
        soundManager.playMatchStart()
        _currentScreen.value = AppScreen.GAME
    }

    fun updateRules(newRules: RulesConfiguration) {
        _rulesConfig.value = newRules
    }

    fun getString(key: String): String {
        return LocalizationManager.getString(_language.value, key)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.stopBgm()
        mascotVoiceManager.release()
    }
}
