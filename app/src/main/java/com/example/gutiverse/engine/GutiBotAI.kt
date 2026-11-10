package com.example.gutiverse.engine

import kotlin.random.Random

enum class BotDifficulty {
    EASY,
    MEDIUM,
    HARD
}

class GutiBotAI(
    private val engine: SholoGutiEngine,
    val difficulty: BotDifficulty = BotDifficulty.MEDIUM
) {
    /**
     * Finds the best move for GutiBot (PLAYER_TWO - Top Orange/Gold beads).
     */
    fun selectMove(state: GameState): BeadMove? {
        val legalMoves = engine.getAllLegalMoves(state, PlayerSide.PLAYER_TWO)
        if (legalMoves.isEmpty()) return null

        val captureMoves = legalMoves.filter { it.capturedId != null }

        return when (difficulty) {
            BotDifficulty.EASY -> {
                // 50% chance to take a capture if available, otherwise pick a random move
                if (captureMoves.isNotEmpty() && Random.nextFloat() > 0.5f) {
                    captureMoves.random()
                } else {
                    legalMoves.random()
                }
            }
            BotDifficulty.MEDIUM -> {
                // Greedy: Always execute captures first, otherwise pick the move that advances towards center
                if (captureMoves.isNotEmpty()) {
                    captureMoves.maxByOrNull { move ->
                        val destNode = engine.topology.nodes[move.toId]
                        // Bonus for landing near center
                        val centerDist = kotlin.math.abs((destNode?.x ?: 0.5f) - 0.5f) +
                                kotlin.math.abs((destNode?.y ?: 0.5f) - 0.5f)
                        100.0f - centerDist
                    } ?: captureMoves.random()
                } else {
                    legalMoves.maxByOrNull { move ->
                        val destNode = engine.topology.nodes[move.toId]
                        val centerDist = kotlin.math.abs((destNode?.x ?: 0.5f) - 0.5f) +
                                kotlin.math.abs((destNode?.y ?: 0.5f) - 0.5f)
                        -centerDist
                    } ?: legalMoves.random()
                }
            }
            BotDifficulty.HARD -> {
                // Minimax with Alpha-Beta Pruning (Depth 3)
                var bestMove: BeadMove? = null
                var bestScore = Int.MIN_VALUE

                // Order capture moves first for alpha-beta efficiency
                val sortedMoves = legalMoves.sortedByDescending { it.capturedId != null }

                for (move in sortedMoves) {
                    val nextState = engine.executeMove(state, move)
                    val score = minimax(nextState, depth = 2, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE, isMaximizing = false)
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = move
                    }
                }
                bestMove ?: legalMoves.random()
            }
        }
    }

    private fun minimax(state: GameState, depth: Int, alpha: Int, beta: Int, isMaximizing: Boolean): Int {
        val winner = state.winner
        if (winner == PlayerSide.PLAYER_TWO) return 10000 + depth
        if (winner == PlayerSide.PLAYER_ONE) return -10000 - depth
        if (depth == 0) return evaluateState(state)

        var curAlpha = alpha
        var curBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            val moves = engine.getAllLegalMoves(state, PlayerSide.PLAYER_TWO)
            if (moves.isEmpty()) return -10000

            for (move in moves) {
                val nextState = engine.executeMove(state, move)
                val eval = minimax(nextState, depth - 1, curAlpha, curBeta, isMaximizing = false)
                maxEval = kotlin.math.max(maxEval, eval)
                curAlpha = kotlin.math.max(curAlpha, eval)
                if (curBeta <= curAlpha) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            val moves = engine.getAllLegalMoves(state, PlayerSide.PLAYER_ONE)
            if (moves.isEmpty()) return 10000

            for (move in moves) {
                val nextState = engine.executeMove(state, move)
                val eval = minimax(nextState, depth - 1, curAlpha, curBeta, isMaximizing = true)
                minEval = kotlin.math.min(minEval, eval)
                curBeta = kotlin.math.min(curBeta, eval)
                if (curBeta <= curAlpha) break
            }
            return minEval
        }
    }

    private fun evaluateState(state: GameState): Int {
        var score = 0
        var p2Count = 0
        var p1Count = 0

        for ((nodeId, side) in state.board) {
            val node = engine.topology.nodes[nodeId] ?: continue
            val centerBonus = (10 - (kotlin.math.abs(node.x - 0.5f) * 10).toInt()) +
                    (10 - (kotlin.math.abs(node.y - 0.5f) * 10).toInt())

            if (side == PlayerSide.PLAYER_TWO) {
                p2Count++
                score += 100 + centerBonus
            } else if (side == PlayerSide.PLAYER_ONE) {
                p1Count++
                score -= 100 + centerBonus
            }
        }

        // Mobility bonus
        val p2Moves = engine.getAllLegalMoves(state, PlayerSide.PLAYER_TWO).size
        val p1Moves = engine.getAllLegalMoves(state, PlayerSide.PLAYER_ONE).size
        score += (p2Moves - p1Moves) * 3

        return score
    }
}
