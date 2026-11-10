package com.example.gutiverse.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Manages GutiBot's multilingual robotic voice lines using Android TextToSpeech.
 *
 * Coordinates automatic background music ducking with [SoundManager].
 */
class MascotVoiceManager(
    private val context: Context,
    private val soundManager: SoundManager
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _currentMascotSpeech = MutableStateFlow<String?>(null)
    val currentMascotSpeech: StateFlow<String?> = _currentMascotSpeech.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {}
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.ENGLISH
            // Robotic futuristic voice tuning
            tts?.setPitch(0.95f)
            tts?.setSpeechRate(1.02f)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    soundManager.setDucked(true)
                }

                override fun onDone(utteranceId: String?) {
                    soundManager.setDucked(false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    soundManager.setDucked(false)
                }
            })
        }
    }

    fun speak(text: String, isWelcome: Boolean = false) {
        _currentMascotSpeech.value = text
        if (!soundManager.isVoiceEnabled) return

        if (isInitialized && tts != null) {
            val utteranceId = "GUTIBOT_${System.currentTimeMillis()}"
            soundManager.setDucked(true)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun speakWelcome() {
        speak(
            "Assalamu Alaikum wa Rahmatullahi wa Barakatuh! Welcome to GUTIVERSE! Enter the ultimate 16 Bead gaming arena. Are you ready to play?",
            isWelcome = true
        )
    }

    fun onCaptureReaction() {
        val lines = listOf(
            "Excellent capture! Keep going!",
            "Great strike! You took down an enemy bead!",
            "Brilliant jump! The arena is watching!"
        )
        speak(lines.random())
    }

    fun onLostBeadReaction() {
        val lines = listOf(
            "Oh no! You've lost a bead. Don't give up!",
            "Careful! Guard your flanks!",
            "A tough loss, but the battle is not over!"
        )
        speak(lines.random())
    }

    fun onGreatMoveReaction() {
        val lines = listOf(
            "Amazing move! Well played!",
            "Strategic positioning! Very impressive!",
            "Solid move! Keep your defenses tight!"
        )
        speak(lines.random())
    }

    fun onNearVictoryReaction() {
        speak("You're getting closer to victory! Stay sharp!")
    }

    fun onVictoryReaction() {
        speak("Congratulations! You're the champion of GUTIVERSE!")
    }

    fun onDefeatReaction() {
        speak("Great effort! Better luck next time!")
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
