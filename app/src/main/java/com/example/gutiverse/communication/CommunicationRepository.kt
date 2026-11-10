package com.example.gutiverse.communication

import com.example.gutiverse.communication.models.CallHistoryItem
import com.example.gutiverse.communication.models.CallType
import com.example.gutiverse.communication.models.ChatMessage
import com.example.gutiverse.communication.models.DeliveryStatus
import com.example.gutiverse.communication.models.GutiverseFriend
import com.example.gutiverse.communication.models.MessageType
import com.example.gutiverse.communication.models.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CommunicationRepository {

    private val currentUserId = "GUTI-7894-BD"
    private val currentUserName = "Touhidul_Rifat"

    // Friends list
    private val _friends = MutableStateFlow<List<GutiverseFriend>>(listOf(
        GutiverseFriend(
            id = "GUTI-1024-DH",
            name = "Tanvir_GutiKing",
            status = UserStatus.ONLINE,
            lastSeenText = "Online",
            isFavorite = true,
            level = 8,
            bio = "Always ready for 16 Guti battle! 🏆"
        ),
        GutiverseFriend(
            id = "GUTI-5521-BD",
            name = "Shakil_Esports",
            status = UserStatus.IN_GAME,
            lastSeenText = "In Match (Turn 12)",
            isFavorite = true,
            level = 10,
            bio = "Competitive Sholo Guti champion."
        ),
        GutiverseFriend(
            id = "GUTI-8832-CTG",
            name = "Arif_Master16",
            status = UserStatus.OFFLINE,
            lastSeenText = "24m ago",
            isFavorite = false,
            level = 5,
            bio = "Learning multi-jump chain combos."
        ),
        GutiverseFriend(
            id = "GUTI-BOT-9999",
            name = "GutiBot Mascot",
            status = UserStatus.ONLINE,
            lastSeenText = "AI Arena Host",
            isFavorite = true,
            level = 99,
            bio = "Official GUTIVERSE AI Assistant & Sparring Partner."
        )
    ))
    val friends: StateFlow<List<GutiverseFriend>> = _friends.asStateFlow()

    // Blocked IDs
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    // Chats map: Friend ID -> List of ChatMessage
    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(mapOf(
        "GUTI-1024-DH" to listOf(
            ChatMessage(
                id = "m1",
                senderId = "GUTI-1024-DH",
                senderName = "Tanvir_GutiKing",
                text = "Assalamu Alaikum brother! Ready for a 16 Guti match?",
                deliveryStatus = DeliveryStatus.READ,
                timestampMillis = System.currentTimeMillis() - 3600000
            ),
            ChatMessage(
                id = "m2",
                senderId = currentUserId,
                senderName = currentUserName,
                text = "Wa Alaikum Assalam! Yes, let's play with multi-jump chain rules!",
                deliveryStatus = DeliveryStatus.READ,
                timestampMillis = System.currentTimeMillis() - 3500000
            ),
            ChatMessage(
                id = "m3",
                senderId = "GUTI-1024-DH",
                senderName = "Tanvir_GutiKing",
                text = "Call me directly while we play so we can strategize! 🎙️",
                deliveryStatus = DeliveryStatus.READ,
                timestampMillis = System.currentTimeMillis() - 300000
            )
        ),
        "GUTI-BOT-9999" to listOf(
            ChatMessage(
                id = "bm1",
                senderId = "GUTI-BOT-9999",
                senderName = "GutiBot Mascot",
                text = "Greetings Warrior Touhidul! 16 Bead Arena is synchronized. Tap call anytime for tactical voice debriefing!",
                deliveryStatus = DeliveryStatus.READ,
                timestampMillis = System.currentTimeMillis() - 7200000
            )
        )
    ))
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    // Call History
    private val _callHistory = MutableStateFlow<List<CallHistoryItem>>(listOf(
        CallHistoryItem(
            id = "c1",
            peerName = "Tanvir_GutiKing",
            peerId = "GUTI-1024-DH",
            callType = CallType.VIDEO,
            isIncoming = true,
            wasMissed = false,
            durationSeconds = 245,
            timestampMillis = System.currentTimeMillis() - 86400000
        ),
        CallHistoryItem(
            id = "c2",
            peerName = "Shakil_Esports",
            peerId = "GUTI-5521-BD",
            callType = CallType.AUDIO,
            isIncoming = false,
            wasMissed = false,
            durationSeconds = 112,
            timestampMillis = System.currentTimeMillis() - 172800000
        ),
        CallHistoryItem(
            id = "c3",
            peerName = "Arif_Master16",
            peerId = "GUTI-8832-CTG",
            callType = CallType.AUDIO,
            isIncoming = true,
            wasMissed = true,
            durationSeconds = 0,
            timestampMillis = System.currentTimeMillis() - 259200000
        )
    ))
    val callHistory: StateFlow<List<CallHistoryItem>> = _callHistory.asStateFlow()

    // Pending friend requests
    private val _pendingRequests = MutableStateFlow<List<String>>(listOf("GUTI-4412-SYL (Fahim_Challenger)"))
    val pendingRequests: StateFlow<List<String>> = _pendingRequests.asStateFlow()

    // Search query result
    fun searchPlayerById(query: String): GutiverseFriend? {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return null
        return _friends.value.firstOrNull { it.id.equals(trimmed, ignoreCase = true) }
            ?: if (trimmed.startsWith("GUTI-", ignoreCase = true)) {
                GutiverseFriend(
                    id = trimmed.uppercase(),
                    name = "Player_${trimmed.takeLast(4)}",
                    status = UserStatus.ONLINE,
                    lastSeenText = "Recently active",
                    level = 3
                )
            } else null
    }

    fun sendFriendRequest(peerId: String) {
        // Mock add
        if (_friends.value.none { it.id == peerId }) {
            val newFriend = GutiverseFriend(
                id = peerId,
                name = "Friend_${peerId.takeLast(4)}",
                status = UserStatus.ONLINE,
                lastSeenText = "Online",
                level = 1
            )
            _friends.value = _friends.value + newFriend
        }
    }

    fun toggleFavorite(friendId: String) {
        _friends.value = _friends.value.map {
            if (it.id == friendId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun toggleBlock(friendId: String) {
        val current = _blockedUserIds.value
        if (current.contains(friendId)) {
            _blockedUserIds.value = current - friendId
        } else {
            _blockedUserIds.value = current + friendId
        }
    }

    fun sendMessage(friendId: String, text: String, type: MessageType = MessageType.TEXT, voiceDurationSeconds: Int = 0, replyToId: String? = null, replyToText: String? = null) {
        val currentList = _chatMessages.value[friendId].orEmpty()
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = currentUserId,
            senderName = currentUserName,
            text = text,
            type = type,
            voiceDurationSeconds = voiceDurationSeconds,
            timestampMillis = System.currentTimeMillis(),
            deliveryStatus = DeliveryStatus.SENT,
            replyToMessageId = replyToId,
            replyToText = replyToText
        )
        val updated = currentList + newMsg
        _chatMessages.value = _chatMessages.value + (friendId to updated)

        // Mock automated response from peer after 1.5 seconds if peer is GutiBot or Tanvir
        if (friendId == "GUTI-BOT-9999" || friendId == "GUTI-1024-DH") {
            kotlinx.coroutines.GlobalScope.let {
                // Update message delivery status to DELIVERED and READ
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    markDelivered(friendId, newMsg.id)
                }, 800)
            }
        }
    }

    private fun markDelivered(friendId: String, msgId: String) {
        val list = _chatMessages.value[friendId].orEmpty().map {
            if (it.id == msgId) it.copy(deliveryStatus = DeliveryStatus.READ) else it
        }
        _chatMessages.value = _chatMessages.value + (friendId to list)
    }

    fun deleteMessage(friendId: String, messageId: String) {
        val list = _chatMessages.value[friendId].orEmpty().filter { it.id != messageId }
        _chatMessages.value = _chatMessages.value + (friendId to list)
    }

    fun addCallHistory(item: CallHistoryItem) {
        _callHistory.value = listOf(item) + _callHistory.value
    }
}
