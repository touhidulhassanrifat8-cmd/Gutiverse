package com.example.gutiverse.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * High-performance, zero-external-dependency Procedural Robotic Audio Engine for GUTIVERSE.
 *
 * Synthesizes crisp cybernetic sound effects, robotic background music, and controls audio ducking.
 */
class SoundManager(private val context: Context) {

    private val audioScope = CoroutineScope(Dispatchers.Default + Job())
    private var bgmJob: Job? = null

    // Audio Settings
    var isMusicEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) stopBgm() else if (isAppForeground) startBgm()
        }

    var isSfxEnabled: Boolean = true
    var isVoiceEnabled: Boolean = true

    var masterVolume: Float = 0.85f
    var musicVolume: Float = 0.50f
    var sfxVolume: Float = 0.80f
    var voiceVolume: Float = 1.0f

    // Music Ducking state (when GutiBot mascot speaks)
    private var isDucked: Boolean = false

    private var isAppForeground: Boolean = false

    private val sampleRate = 22050

    // Vibrator for haptic tactile feedback
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun onResume() {
        isAppForeground = true
        if (isMusicEnabled) {
            startBgm()
        }
    }

    fun onPause() {
        isAppForeground = false
        stopBgm()
    }

    fun setDucked(ducked: Boolean) {
        isDucked = ducked
    }

    /**
     * Loops a cybernetic ambient arpeggio background soundtrack.
     */
    fun startBgm() {
        if (bgmJob?.isActive == true || !isMusicEnabled) return

        bgmJob = audioScope.launch {
            // Cyber chord progression frequencies (Hz)
            val bassLine = floatArrayOf(110f, 110f, 130.81f, 98f, 110f, 110f, 146.83f, 130.81f)
            val arpeggioNotes = floatArrayOf(
                220f, 261.63f, 329.63f, 440f, 523.25f, 440f, 329.63f, 261.63f,
                196f, 246.94f, 293.66f, 392f, 493.88f, 392f, 293.66f, 246.94f
            )

            var bassIdx = 0
            var arpIdx = 0

            while (isActive && isMusicEnabled) {
                val effectiveVol = if (isDucked) {
                    masterVolume * musicVolume * 0.20f
                } else {
                    masterVolume * musicVolume
                }

                if (effectiveVol > 0.01f) {
                    val bassFreq = bassLine[bassIdx % bassLine.size]
                    val arpFreq = arpeggioNotes[arpIdx % arpeggioNotes.size]

                    playSynthesizedTone(
                        freq1 = bassFreq,
                        freq2 = arpFreq,
                        durationMs = 180,
                        volume = effectiveVol * 0.35f,
                        decay = true
                    )
                }

                arpIdx++
                if (arpIdx % 2 == 0) bassIdx++
                delay(190)
            }
        }
    }

    fun stopBgm() {
        bgmJob?.cancel()
        bgmJob = null
    }

    // --- Sound Effects ---

    fun playButtonClick() {
        if (!isSfxEnabled) return
        vibrate(15)
        audioScope.launch {
            playChirp(startFreq = 1400f, endFreq = 900f, durationMs = 45, volume = masterVolume * sfxVolume * 0.45f)
        }
    }

    fun playMenuTransition() {
        if (!isSfxEnabled) return
        audioScope.launch {
            playChirp(startFreq = 300f, endFreq = 700f, durationMs = 90, volume = masterVolume * sfxVolume * 0.35f)
        }
    }

    fun playBeadSelect() {
        if (!isSfxEnabled) return
        vibrate(20)
        audioScope.launch {
            playDualTone(880f, 1320f, durationMs = 80, volume = masterVolume * sfxVolume * 0.50f)
        }
    }

    fun playBeadMove() {
        if (!isSfxEnabled) return
        vibrate(25)
        audioScope.launch {
            playChirp(startFreq = 440f, endFreq = 660f, durationMs = 70, volume = masterVolume * sfxVolume * 0.60f)
        }
    }

    fun playBeadCapture() {
        if (!isSfxEnabled) return
        vibrate(50)
        audioScope.launch {
            playNoiseImpact(durationMs = 120, volume = masterVolume * sfxVolume * 0.75f)
        }
    }

    fun playTurnChange() {
        if (!isSfxEnabled) return
        vibrate(15)
        audioScope.launch {
            playDualTone(523.25f, 659.25f, durationMs = 60, volume = masterVolume * sfxVolume * 0.40f)
        }
    }

    fun playBeadLost() {
        if (!isSfxEnabled) return
        vibrate(40)
        audioScope.launch {
            playChirp(startFreq = 380f, endFreq = 180f, durationMs = 140, volume = masterVolume * sfxVolume * 0.60f)
        }
    }

    fun playMatchStart() {
        if (!isSfxEnabled) return
        vibrate(60)
        audioScope.launch {
            playChirp(startFreq = 220f, endFreq = 880f, durationMs = 250, volume = masterVolume * sfxVolume * 0.70f)
        }
    }

    fun playVictory() {
        if (!isSfxEnabled) return
        vibrate(100)
        audioScope.launch {
            val victoryNotes = floatArrayOf(440f, 554.37f, 659.25f, 880f)
            for (note in victoryNotes) {
                playDualTone(note, note * 1.5f, durationMs = 120, volume = masterVolume * sfxVolume * 0.70f)
                delay(130)
            }
        }
    }

    fun playDefeat() {
        if (!isSfxEnabled) return
        vibrate(80)
        audioScope.launch {
            val defeatNotes = floatArrayOf(440f, 415.30f, 370f, 329.63f)
            for (note in defeatNotes) {
                playDualTone(note, note * 0.5f, durationMs = 140, volume = masterVolume * sfxVolume * 0.65f)
                delay(150)
            }
        }
    }

    // --- Audio Synthesis Engine ---

    private fun playChirp(startFreq: Float, endFreq: Float, durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0f)).toInt()
        val buffer = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * Math.PI * currentFreq / sampleRate
            val envelope = 1.0 - progress
            val sample = (sin(phase) * envelope * Short.MAX_VALUE * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeAudioTrack(buffer)
    }

    private fun playDualTone(freq1: Float, freq2: Float, durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0f)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val envelope = kotlin.math.exp(-progress * 3.0)
            val s1 = sin(2.0 * Math.PI * freq1 * t)
            val s2 = sin(2.0 * Math.PI * freq2 * t) * 0.6
            val sample = ((s1 + s2) * 0.5 * envelope * Short.MAX_VALUE * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeAudioTrack(buffer)
    }

    private fun playSynthesizedTone(freq1: Float, freq2: Float, durationMs: Int, volume: Float, decay: Boolean) {
        val numSamples = (sampleRate * (durationMs / 1000.0f)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val env = if (decay) kotlin.math.exp(-progress * 2.5) else 1.0
            val s1 = sin(2.0 * Math.PI * freq1 * t) * 0.7
            val s2 = sin(2.0 * Math.PI * freq2 * t) * 0.3
            val sample = ((s1 + s2) * env * Short.MAX_VALUE * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeAudioTrack(buffer)
    }

    private fun playNoiseImpact(durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0f)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val env = kotlin.math.exp(-progress * 6.0)
            val noise = (kotlin.random.Random.nextDouble() * 2.0 - 1.0) * 0.5
            val tone = sin(2.0 * Math.PI * (800.0 - progress * 500.0) * (i.toDouble() / sampleRate)) * 0.5
            val sample = ((noise + tone) * env * Short.MAX_VALUE * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writeAudioTrack(buffer)
    }

    private fun writeAudioTrack(buffer: ShortArray) {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val trackBufferSize = kotlin.math.max(buffer.size * 2, minBufferSize)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(trackBufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            audioScope.launch {
                delay((buffer.size * 1000L / sampleRate) + 50L)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Silently fall back if audio output device is unavailable
        }
    }

    private fun vibrate(ms: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(ms)
            }
        } catch (_: Exception) {}
    }
}
