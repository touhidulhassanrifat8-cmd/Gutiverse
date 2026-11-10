package com.example.gutiverse.engine

enum class PlayerSide {
    PLAYER_ONE, // Player One (Green beads, bottom)
    PLAYER_TWO, // Player Two (Orange/Gold beads, top)
    NONE;

    fun opponent(): PlayerSide = when (this) {
        PLAYER_ONE -> PLAYER_TWO
        PLAYER_TWO -> PLAYER_ONE
        NONE -> NONE
    }
}

data class BeadMove(
    val fromId: Int,
    val toId: Int,
    val capturedId: Int? = null
)

data class GameState(
    val board: Map<Int, PlayerSide>,
    val activePlayer: PlayerSide = PlayerSide.PLAYER_ONE,
    val playerOneCaptured: Int = 0, // Green's score: number of Orange/Gold beads captured
    val playerTwoCaptured: Int = 0, // Orange's score: number of Green beads captured
    val selectedNodeId: Int? = null,
    val legalTargetIds: Set<Int> = emptySet(),
    val activeJumpingNodeId: Int? = null, // Set during multi-jump chain
    val winner: PlayerSide? = null,
    val moveHistoryCount: Int = 0
)

class SholoGutiEngine(
    val topology: BoardTopology = BoardTopology.createStandard37(),
    var rules: RulesConfiguration = RulesConfiguration.DEFAULT
) {
    /**
     * Initializes the traditional 16 vs 16 board (32 beads total across 37 points).
     * Player Two (Top): Exactly 16 Orange/Gold beads.
     * Middle Row (Row 4): Exactly 5 neutral empty spots.
     * Player One (Bottom): Exactly 16 Green beads.
     */
    fun createInitialState(): GameState {
        val initialBoard = mutableMapOf<Int, PlayerSide>()
        for ((id, node) in topology.nodes) {
            when {
                // Top apex (row 0), top slice (row 1), and top 2 grid rows (rows 2, 3) -> PLAYER_TWO (16 beads)
                node.row <= 3 -> initialBoard[id] = PlayerSide.PLAYER_TWO
                // Middle row (row 4) -> Neutral empty (5 spots)
                node.row == 4 -> initialBoard[id] = PlayerSide.NONE
                // Bottom 2 grid rows (rows 5, 6), bottom slice (row 7), bottom apex (row 8) -> PLAYER_ONE (16 beads)
                else -> initialBoard[id] = PlayerSide.PLAYER_ONE
            }
        }
        return GameState(board = initialBoard)
    }

    /**
     * Returns all legal moves for a given piece at [fromNodeId].
     */
    fun getLegalMovesForNode(state: GameState, fromNodeId: Int): List<BeadMove> {
        val piece = state.board[fromNodeId] ?: return emptyList()
        if (piece != state.activePlayer) return emptyList()

        // If in multi-jump mode, only the current jumping bead can move, and ONLY jumps are allowed
        if (state.activeJumpingNodeId != null) {
            if (fromNodeId != state.activeJumpingNodeId) return emptyList()
            return getJumpMovesForNode(state, fromNodeId)
        }

        val moves = mutableListOf<BeadMove>()

        // 1. Jump captures
        val jumpMoves = getJumpMovesForNode(state, fromNodeId)
        moves.addAll(jumpMoves)

        // 2. Normal 1-step moves along connected lines
        if (!rules.mandatoryCapture || getAllJumpMoves(state, state.activePlayer).isEmpty()) {
            val neighbors = topology.adjacency[fromNodeId] ?: emptySet()
            for (neighborId in neighbors) {
                if (state.board[neighborId] == PlayerSide.NONE) {
                    moves.add(BeadMove(fromId = fromNodeId, toId = neighborId, capturedId = null))
                }
            }
        }

        return moves
    }

    /**
     * Returns only legal jump captures from [fromNodeId].
     */
    fun getJumpMovesForNode(state: GameState, fromNodeId: Int): List<BeadMove> {
        val piece = state.board[fromNodeId] ?: return emptyList()
        val opponent = piece.opponent()
        val possibleJumps = topology.jumpMapFromNode[fromNodeId] ?: emptyList()

        val validJumps = mutableListOf<BeadMove>()
        for (jump in possibleJumps) {
            val overPiece = state.board[jump.overNodeId]
            val destPiece = state.board[jump.toNodeId]

            if (overPiece == opponent && destPiece == PlayerSide.NONE) {
                validJumps.add(
                    BeadMove(
                        fromId = jump.fromNodeId,
                        toId = jump.toNodeId,
                        capturedId = jump.overNodeId
                    )
                )
            }
        }
        return validJumps
    }

    /**
     * Returns all jump moves currently available for [player].
     */
    fun getAllJumpMoves(state: GameState, player: PlayerSide): List<BeadMove> {
        val result = mutableListOf<BeadMove>()
        for ((nodeId, side) in state.board) {
            if (side == player) {
                result.addAll(getJumpMovesForNode(state, nodeId))
            }
        }
        return result
    }

    /**
     * Returns all legal moves currently available for [player].
     */
    fun getAllLegalMoves(state: GameState, player: PlayerSide): List<BeadMove> {
        if (state.activeJumpingNodeId != null) {
            return getJumpMovesForNode(state, state.activeJumpingNodeId)
        }
        val result = mutableListOf<BeadMove>()
        for ((nodeId, side) in state.board) {
            if (side == player) {
                result.addAll(getLegalMovesForNode(state, nodeId))
            }
        }
        return result
    }

    /**
     * Executes a move and produces an updated GameState.
     */
    fun executeMove(state: GameState, move: BeadMove): GameState {
        val newBoard = state.board.toMutableMap()
        val player = state.activePlayer
        newBoard[move.fromId] = PlayerSide.NONE
        newBoard[move.toId] = player

        var newP1Captured = state.playerOneCaptured
        var newP2Captured = state.playerTwoCaptured

        if (move.capturedId != null) {
            newBoard[move.capturedId] = PlayerSide.NONE
            if (player == PlayerSide.PLAYER_ONE) {
                newP1Captured++
            } else {
                newP2Captured++
            }
        }

        // Check if multi-jump continuation is possible
        if (move.capturedId != null && rules.allowMultiJump) {
            val tempState = state.copy(board = newBoard)
            val subsequentJumps = getJumpMovesForNode(tempState, move.toId)
            if (subsequentJumps.isNotEmpty()) {
                // Continue multi-jump with the same bead
                return state.copy(
                    board = newBoard,
                    playerOneCaptured = newP1Captured,
                    playerTwoCaptured = newP2Captured,
                    selectedNodeId = move.toId,
                    legalTargetIds = subsequentJumps.map { it.toId }.toSet(),
                    activeJumpingNodeId = move.toId,
                    moveHistoryCount = state.moveHistoryCount + 1
                )
            }
        }

        // Turn complete, switch to next player
        val nextPlayer = player.opponent()
        val nextState = state.copy(
            board = newBoard,
            activePlayer = nextPlayer,
            playerOneCaptured = newP1Captured,
            playerTwoCaptured = newP2Captured,
            selectedNodeId = null,
            legalTargetIds = emptySet(),
            activeJumpingNodeId = null,
            moveHistoryCount = state.moveHistoryCount + 1
        )

        // Check victory conditions
        val winner = checkWinner(nextState)
        return nextState.copy(winner = winner)
    }

    /**
     * Ends the turn early during a multi-jump sequence.
     */
    fun endMultiJumpTurn(state: GameState): GameState {
        if (state.activeJumpingNodeId == null) return state
        val nextPlayer = state.activePlayer.opponent()
        val nextState = state.copy(
            activePlayer = nextPlayer,
            selectedNodeId = null,
            legalTargetIds = emptySet(),
            activeJumpingNodeId = null
        )
        return nextState.copy(winner = checkWinner(nextState))
    }

    /**
     * Checks if either side has won.
     */
    fun checkWinner(state: GameState): PlayerSide? {
        val p1Count = state.board.values.count { it == PlayerSide.PLAYER_ONE }
        val p2Count = state.board.values.count { it == PlayerSide.PLAYER_TWO }

        if (p1Count == 0) return PlayerSide.PLAYER_TWO
        if (p2Count == 0) return PlayerSide.PLAYER_ONE

        // Check if active player has no legal moves remaining
        val legalMoves = getAllLegalMoves(state, state.activePlayer)
        if (legalMoves.isEmpty()) {
            return state.activePlayer.opponent()
        }

        return null
    }
}
