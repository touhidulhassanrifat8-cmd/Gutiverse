package com.example.gutiverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gutiverse.audio.MascotVoiceManager
import com.example.gutiverse.audio.SoundManager
import com.example.gutiverse.engine.BotDifficulty
import com.example.gutiverse.engine.GutiBotAI
import com.example.gutiverse.engine.GameState
import com.example.gutiverse.engine.PlayerSide
import com.example.gutiverse.engine.RulesConfiguration
import com.example.gutiverse.engine.SholoGutiEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    val gameMode: GameMode,
    val botDifficulty: BotDifficulty,
    val rules: RulesConfiguration,
    val soundManager: SoundManager,
    val mascotVoiceManager: MascotVoiceManager
) : ViewModel() {

    val engine = SholoGutiEngine(rules = rules)
    val botAI = GutiBotAI(engine, botDifficulty)

    private val _gameState = MutableStateFlow(engine.createInitialState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _turnTimeRemaining = MutableStateFlow(rules.turnTimerSeconds)
    val turnTimeRemaining: StateFlow<Int> = _turnTimeRemaining.asStateFlow()

    private val _matchTimeRemaining = MutableStateFlow(rules.matchTimerSeconds)
    val matchTimeRemaining: StateFlow<Int> = _matchTimeRemaining.asStateFlow()

    private var timerJob: Job? = null
    private var aiMoveJob: Job? = null

    // True if top player is computer/AI
    val isVsAi: Boolean
        get() = gameMode == GameMode.ROBOT || gameMode == GameMode.VS_COMPUTER || gameMode == GameMode.SHOLO_GUTI_CLASSIC

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_gameState.value.winner != null) break

                _turnTimeRemaining.value = (_turnTimeRemaining.value - 1).coerceAtLeast(0)
                _matchTimeRemaining.value = (_matchTimeRemaining.value - 1).coerceAtLeast(0)

                // Turn timeout: switch active player
                if (_turnTimeRemaining.value == 0) {
                    onTurnTimeout()
                }
            }
        }
    }

    private fun onTurnTimeout() {
        val current = _gameState.value
        val nextPlayer = current.activePlayer.opponent()
        _gameState.value = current.copy(
            activePlayer = nextPlayer,
            selectedNodeId = null,
            legalTargetIds = emptySet(),
            activeJumpingNodeId = null
        )
        _turnTimeRemaining.value = rules.turnTimerSeconds
        soundManager.playTurnChange()
        if (isVsAi && nextPlayer == PlayerSide.PLAYER_TWO) {
            checkAITurn()
        }
    }

    fun onNodeClicked(nodeId: Int) {
        val state = _gameState.value
        if (state.winner != null) return

        // Prevent human input during AI turn in AI matches
        if (isVsAi && state.activePlayer == PlayerSide.PLAYER_TWO) {
            return
        }

        // If player has already selected a bead and taps a legal target node -> execute move
        if (state.selectedNodeId != null && state.legalTargetIds.contains(nodeId)) {
            val legalMoves = engine.getLegalMovesForNode(state, state.selectedNodeId)
            val chosenMove = legalMoves.find { it.toId == nodeId }
            if (chosenMove != null) {
                executeUserMove(chosenMove)
                return
            }
        }

        // Cannot switch pieces during multi-jump sequence
        if (state.activeJumpingNodeId != null) return

        // Tap on player's own piece -> Select it
        val pieceAtNode = state.board[nodeId]
        if (pieceAtNode == state.activePlayer) {
            val legalMoves = engine.getLegalMovesForNode(state, nodeId)
            soundManager.playBeadSelect()
            _gameState.value = state.copy(
                selectedNodeId = nodeId,
                legalTargetIds = legalMoves.map { it.toId }.toSet()
            )
        } else {
            // Tap on empty space or enemy piece without legal move -> Deselect
            _gameState.value = state.copy(
                selectedNodeId = null,
                legalTargetIds = emptySet()
            )
        }
    }

    private fun executeUserMove(move: com.example.gutiverse.engine.BeadMove) {
        val prevState = _gameState.value
        val newState = engine.executeMove(prevState, move)
        _gameState.value = newState

        if (move.capturedId != null) {
            soundManager.playBeadCapture()
            mascotVoiceManager.onCaptureReaction()
        } else {
            soundManager.playBeadMove()
        }

        _turnTimeRemaining.value = rules.turnTimerSeconds

        // Check if game won
        if (newState.winner != null) {
            if (newState.winner == PlayerSide.PLAYER_ONE) {
                soundManager.playVictory()
                mascotVoiceManager.onVictoryReaction()
            } else {
                soundManager.playDefeat()
                mascotVoiceManager.onDefeatReaction()
            }
            return
        }

        // Auto-capture if a second (or subsequent) jump capture is available on a single path
        if (newState.activeJumpingNodeId != null && newState.legalTargetIds.size == 1) {
            val nextTargetId = newState.legalTargetIds.first()
            val nextMoves = engine.getLegalMovesForNode(newState, newState.activeJumpingNodeId)
            val nextJumpMove = nextMoves.find { it.toId == nextTargetId }
            if (nextJumpMove != null) {
                viewModelScope.launch {
                    delay(120)
                    // Ensure state hasn't changed (e.g. timeout or restart)
                    if (_gameState.value.activeJumpingNodeId == newState.activeJumpingNodeId &&
                        _gameState.value.winner == null
                    ) {
                        executeUserMove(nextJumpMove)
                    }
                }
                return
            }
        }

        // Play turn change sound if turn actually switched (not during multi-jump chain)
        if (newState.activeJumpingNodeId == null && newState.activePlayer != prevState.activePlayer) {
            soundManager.playTurnChange()
        }

        // If turn has switched to AI, trigger AI move
        if (isVsAi && newState.activePlayer == PlayerSide.PLAYER_TWO) {
            checkAITurn()
        }
    }

    fun endMultiJumpTurn() {
        val state = _gameState.value
        if (state.activeJumpingNodeId == null) return
        val nextState = engine.endMultiJumpTurn(state)
        _gameState.value = nextState
        _turnTimeRemaining.value = rules.turnTimerSeconds
        soundManager.playTurnChange()

        if (nextState.winner != null) {
            soundManager.playVictory()
            return
        }

        if (isVsAi && nextState.activePlayer == PlayerSide.PLAYER_TWO) {
            checkAITurn()
        }
    }

    private fun checkAITurn() {
        aiMoveJob?.cancel()
        aiMoveJob = viewModelScope.launch {
            delay(250) // Fast AI response
            val currentState = _gameState.value
            if (currentState.winner != null || currentState.activePlayer != PlayerSide.PLAYER_TWO) return@launch

            val aiMove = botAI.selectMove(currentState)
            if (aiMove != null) {
                val stateAfterMove = engine.executeMove(currentState, aiMove)
                _gameState.value = stateAfterMove

                if (aiMove.capturedId != null) {
                    soundManager.playBeadLost()
                    mascotVoiceManager.onLostBeadReaction()
                } else {
                    soundManager.playBeadMove()
                }

                _turnTimeRemaining.value = rules.turnTimerSeconds

                // Handle AI multi-jump chain if available
                var chainState = stateAfterMove
                while (chainState.activeJumpingNodeId != null && chainState.winner == null) {
                    delay(180)
                    val nextJump = botAI.selectMove(chainState)
                    if (nextJump != null && nextJump.capturedId != null) {
                        chainState = engine.executeMove(chainState, nextJump)
                        _gameState.value = chainState
                        soundManager.playBeadLost()
                    } else {
                        chainState = engine.endMultiJumpTurn(chainState)
                        _gameState.value = chainState
                        break
                    }
                }

                // If turn passed back to Player One, play turn change sound
                if (chainState.winner == null && chainState.activePlayer == PlayerSide.PLAYER_ONE) {
                    soundManager.playTurnChange()
                }

                if (chainState.winner != null) {
                    if (chainState.winner == PlayerSide.PLAYER_TWO) {
                        soundManager.playDefeat()
                        mascotVoiceManager.onDefeatReaction()
                    } else {
                        soundManager.playVictory()
                        mascotVoiceManager.onVictoryReaction()
                    }
                }
            } else {
                // No legal moves -> PLAYER_ONE wins
                _gameState.value = currentState.copy(winner = PlayerSide.PLAYER_ONE)
                soundManager.playVictory()
            }
        }
    }

    fun restartMatch() {
        _gameState.value = engine.createInitialState()
        _turnTimeRemaining.value = rules.turnTimerSeconds
        _matchTimeRemaining.value = rules.matchTimerSeconds
        soundManager.playMatchStart()
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        aiMoveJob?.cancel()
    }
}
