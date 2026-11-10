package com.example

import com.example.gutiverse.engine.BeadMove
import com.example.gutiverse.engine.BoardTopology
import com.example.gutiverse.engine.BotDifficulty
import com.example.gutiverse.engine.GameState
import com.example.gutiverse.engine.GutiBotAI
import com.example.gutiverse.engine.PlayerSide
import com.example.gutiverse.engine.RulesConfiguration
import com.example.gutiverse.engine.SholoGutiEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testInitial37NodePieceDistribution() {
        val engine = SholoGutiEngine()
        val initialState = engine.createInitialState()

        // 1. Board has exactly 37 total intersection points (nodes)
        assertEquals(37, initialState.board.size)

        // 2. Player Two (Orange/Gold beads, top) starts with exactly 16 beads
        val p2Count = initialState.board.values.count { it == PlayerSide.PLAYER_TWO }
        assertEquals(16, p2Count)

        // 3. Player One (Green beads, bottom) starts with exactly 16 beads
        val p1Count = initialState.board.values.count { it == PlayerSide.PLAYER_ONE }
        assertEquals(16, p1Count)

        // 4. Total beads in the match must be exactly 32 (16 + 16)
        val totalBeads = p2Count + p1Count
        assertEquals(32, totalBeads)

        // 5. Exactly 5 center neutral points on row 4 start empty
        val emptyCount = initialState.board.values.count { it == PlayerSide.NONE }
        assertEquals(5, emptyCount)

        // 6. 32 total beads + 5 empty spots = 37 board intersection points
        assertEquals(37, totalBeads + emptyCount)

        // Player One (Green) starts first
        assertEquals(PlayerSide.PLAYER_ONE, initialState.activePlayer)
    }

    @Test
    fun testLegalOpeningMoves() {
        val engine = SholoGutiEngine()
        val initialState = engine.createInitialState()

        // Player One (Green) has legal opening moves along connected lines into the 5 empty spots
        val legalMoves = engine.getAllLegalMoves(initialState, PlayerSide.PLAYER_ONE)
        assertTrue("Player One should have legal opening moves into row 4", legalMoves.isNotEmpty())

        // Ensure no illegal captures are possible on turn 1
        val captures = legalMoves.filter { it.capturedId != null }
        assertTrue("No captures possible on immediate initial turn", captures.isEmpty())
    }

    @Test
    fun testJumpCaptureAndMultiJumpChain() {
        val topology = BoardTopology.createStandard37()
        val rules = RulesConfiguration(allowMultiJump = true)
        val engine = SholoGutiEngine(topology, rules)

        // Setup custom collinear jump scenario:
        // Node 23: Player One (Green bead)
        // Node 18: Player Two (Orange bead, adjacent to 23)
        // Node 13: Empty spot behind 18
        // Node 8: Player Two (Orange bead, adjacent to 13)
        // Node 3: Empty spot behind 8
        val customBoard = mutableMapOf<Int, PlayerSide>()
        for (i in 0 until 37) {
            customBoard[i] = PlayerSide.NONE
        }
        customBoard[23] = PlayerSide.PLAYER_ONE
        customBoard[18] = PlayerSide.PLAYER_TWO
        customBoard[8] = PlayerSide.PLAYER_TWO

        val testState = GameState(board = customBoard, activePlayer = PlayerSide.PLAYER_ONE)

        // 1. Verify first jump is detected from 23 over 18 to 13
        val jumpsFrom23 = engine.getJumpMovesForNode(testState, 23)
        assertTrue(jumpsFrom23.any { it.toId == 13 && it.capturedId == 18 })

        // 2. Execute first jump
        val firstMove = jumpsFrom23.first { it.toId == 13 && it.capturedId == 18 }
        val stateAfterFirstJump = engine.executeMove(testState, firstMove)

        // Orange bead at 18 must be captured (NONE)
        assertEquals(PlayerSide.NONE, stateAfterFirstJump.board[18])
        assertEquals(PlayerSide.PLAYER_ONE, stateAfterFirstJump.board[13])

        // Multi-jump continuation should be active on node 13
        assertEquals(13, stateAfterFirstJump.activeJumpingNodeId)

        // 3. Verify second jump is detected from 13 over 8 to 3
        val subsequentJumps = engine.getJumpMovesForNode(stateAfterFirstJump, 13)
        assertTrue(subsequentJumps.any { it.toId == 3 && it.capturedId == 8 })

        // 4. Execute second jump
        val secondMove = subsequentJumps.first { it.toId == 3 && it.capturedId == 8 }
        val stateAfterSecondJump = engine.executeMove(stateAfterFirstJump, secondMove)

        // Orange bead at 8 must be captured (NONE)
        assertEquals(PlayerSide.NONE, stateAfterSecondJump.board[8])
        assertEquals(PlayerSide.PLAYER_ONE, stateAfterSecondJump.board[3])
        assertEquals(2, stateAfterSecondJump.playerOneCaptured)
    }

    @Test
    fun testGutiBotAIDecision() {
        val engine = SholoGutiEngine()
        val botEasy = GutiBotAI(engine, BotDifficulty.EASY)
        val botMedium = GutiBotAI(engine, BotDifficulty.MEDIUM)
        val botHard = GutiBotAI(engine, BotDifficulty.HARD)

        val initialState = engine.createInitialState()
        val p2TurnState = initialState.copy(activePlayer = PlayerSide.PLAYER_TWO)

        val easyMove = botEasy.selectMove(p2TurnState)
        val mediumMove = botMedium.selectMove(p2TurnState)
        val hardMove = botHard.selectMove(p2TurnState)

        assertNotNull(easyMove)
        assertNotNull(mediumMove)
        assertNotNull(hardMove)
    }
}
