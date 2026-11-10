package com.example.gutiverse.communication.models

enum class UserStatus {
    ONLINE,
    IN_GAME,
    OFFLINE
}

data class GutiverseFriend(
    val id: String,
    val name: String,
    val avatarResId: Int? = null,
    val status: UserStatus = UserStatus.ONLINE,
    val lastSeenText: String = "Just now",
    val isFavorite: Boolean = false,
    val isBlocked: Boolean = false,
    val level: Int = 1,
    val bio: String = "GUTIVERSE 16 Bead Warrior"
)

enum class MessageType {
    TEXT,
    PHOTO,
    VOICE,
    STICKER
}

enum class DeliveryStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val voiceDurationSeconds: Int = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT,
    val replyToMessageId: String? = null,
    val replyToText: String? = null
)

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallStatus {
    IDLE,
    DIALING,
    INCOMING,
    RINGING,
    CONNECTED,
    ENDED
}

data class CallSession(
    val callId: String,
    val peerId: String,
    val peerName: String,
    val peerAvatarResId: Int? = null,
    val callType: CallType,
    val status: CallStatus,
    val isIncoming: Boolean,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isCameraOn: Boolean = true,
    val isFrontCamera: Boolean = true,
    val durationSeconds: Int = 0,
    val connectionQuality: String = "HD Excellent (42ms)"
)

data class CallHistoryItem(
    val id: String,
    val peerName: String,
    val peerId: String,
    val callType: CallType,
    val isIncoming: Boolean,
    val wasMissed: Boolean,
    val durationSeconds: Int,
    val timestampMillis: Long = System.currentTimeMillis()
)
