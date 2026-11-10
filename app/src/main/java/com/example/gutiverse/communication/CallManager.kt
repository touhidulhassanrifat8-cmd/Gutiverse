package com.example.gutiverse.communication

import android.content.Context
import com.example.gutiverse.audio.SoundManager
import com.example.gutiverse.communication.models.CallHistoryItem
import com.example.gutiverse.communication.models.CallSession
import com.example.gutiverse.communication.models.CallStatus
import com.example.gutiverse.communication.models.CallType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class CallManager(
    private val context: Context,
    private val soundManager: SoundManager,
    private val repository: CommunicationRepository
) {
    val liveKitService = LiveKitCallService(context)

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null

    private val _currentCall = MutableStateFlow<CallSession?>(null)
    val currentCall: StateFlow<CallSession?> = _currentCall.asStateFlow()

    // Floating window state
    private val _isVideoMinimized = MutableStateFlow(false)
    val isVideoMinimized: StateFlow<Boolean> = _isVideoMinimized.asStateFlow()

    fun setVideoMinimized(minimized: Boolean) {
        _isVideoMinimized.value = minimized
    }

    fun startOutgoingCall(peerId: String, peerName: String, type: CallType) {
        val call = CallSession(
            callId = UUID.randomUUID().toString(),
            peerId = peerId,
            peerName = peerName,
            callType = type,
            status = CallStatus.DIALING,
            isIncoming = false,
            isMuted = false,
            isSpeakerOn = true,
            isCameraOn = (type == CallType.VIDEO),
            isFrontCamera = true,
            durationSeconds = 0,
            connectionQuality = "LiveKit HD 60fps • 24ms"
        )
        _currentCall.value = call
        _isVideoMinimized.value = false
        soundManager.playButtonClick()

        liveKitService.connectToRoom(
            matchId = "arena-16",
            participantId = "GUTI-7894-BD",
            remoteId = peerId,
            remoteName = peerName
        )

        // Simulated quick connect after dialing
        timerJob?.cancel()
        coroutineScope.launch {
            delay(2000)
            if (_currentCall.value?.callId == call.callId && _currentCall.value?.status == CallStatus.DIALING) {
                connectCall()
            }
        }
    }

    fun triggerSimulatedIncomingCall(peerId: String = "GUTI-1024-DH", peerName: String = "Tanvir_GutiKing", type: CallType = CallType.VIDEO) {
        val call = CallSession(
            callId = UUID.randomUUID().toString(),
            peerId = peerId,
            peerName = peerName,
            callType = type,
            status = CallStatus.INCOMING,
            isIncoming = true,
            isMuted = false,
            isSpeakerOn = true,
            isCameraOn = (type == CallType.VIDEO),
            isFrontCamera = true,
            durationSeconds = 0,
            connectionQuality = "LiveKit HD 60fps • 24ms"
        )
        _currentCall.value = call
        _isVideoMinimized.value = false
        soundManager.playBeadSelect()
    }

    fun answerCall() {
        val call = _currentCall.value ?: return
        if (call.status == CallStatus.INCOMING) {
            connectCall()
        }
    }

    private fun connectCall() {
        val current = _currentCall.value ?: return
        _currentCall.value = current.copy(status = CallStatus.CONNECTED)
        soundManager.playMatchStart()

        liveKitService.setSpeakerphoneOn(current.isSpeakerOn)
        liveKitService.setMicrophoneMute(current.isMuted)

        // Start duration timer
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            while (isActive && _currentCall.value?.status == CallStatus.CONNECTED) {
                delay(1000)
                _currentCall.value = _currentCall.value?.let {
                    it.copy(durationSeconds = it.durationSeconds + 1)
                }
            }
        }
    }

    fun rejectOrEndCall() {
        val call = _currentCall.value
        timerJob?.cancel()
        liveKitService.disconnect()
        if (call != null) {
            repository.addCallHistory(
                CallHistoryItem(
                    id = call.callId,
                    peerName = call.peerName,
                    peerId = call.peerId,
                    callType = call.callType,
                    isIncoming = call.isIncoming,
                    wasMissed = call.status == CallStatus.INCOMING,
                    durationSeconds = call.durationSeconds
                )
            )
            soundManager.playButtonClick()
        }
        _currentCall.value = null
        _isVideoMinimized.value = false
    }

    fun toggleMute() {
        _currentCall.value = _currentCall.value?.let {
            val newMute = !it.isMuted
            liveKitService.setMicrophoneMute(newMute)
            it.copy(isMuted = newMute)
        }
    }

    fun toggleSpeaker() {
        _currentCall.value = _currentCall.value?.let {
            val newSpeaker = !it.isSpeakerOn
            liveKitService.setSpeakerphoneOn(newSpeaker)
            it.copy(isSpeakerOn = newSpeaker)
        }
    }

    fun toggleCamera() {
        _currentCall.value = _currentCall.value?.let { it.copy(isCameraOn = !it.isCameraOn) }
    }

    fun switchCamera() {
        _currentCall.value = _currentCall.value?.let { it.copy(isFrontCamera = !it.isFrontCamera) }
    }
}
