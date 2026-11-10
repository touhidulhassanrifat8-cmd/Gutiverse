package com.example.gutiverse.engine

/**
 * Represents a single point on the board.
 * Coordinates are normalized to [0.0f, 1.0f] for responsive rendering across all screen sizes.
 */
data class BoardNode(
    val id: Int,
    val row: Int,
    val col: Int,
    val x: Float,
    val y: Float,
    val name: String = "P$id"
)

/**
 * Represents a legal jump capture over an opponent piece.
 * From [fromNodeId], jumping over [overNodeId], landing on [toNodeId].
 */
data class JumpPath(
    val fromNodeId: Int,
    val overNodeId: Int,
    val toNodeId: Int
)

/**
 * Fully configurable Board Topology for 16 Bead (Sholo Guti).
 *
 * Traditional Sholo Guti (37 Nodes Standard):
 * - Row 0: Top Apex (1 node at center col 2)
 * - Row 1: Top Triangle Middle Slice (5 nodes, cols 0..4)
 * - Row 2: Central Grid Row 0 (5 nodes, cols 0..4)
 * - Row 3: Central Grid Row 1 (5 nodes, cols 0..4)
 * - Row 4: Central Grid Row 2 (5 nodes, cols 0..4 - initial empty neutral zone)
 * - Row 5: Central Grid Row 3 (5 nodes, cols 0..4)
 * - Row 6: Central Grid Row 4 (5 nodes, cols 0..4)
 * - Row 7: Bottom Triangle Middle Slice (5 nodes, cols 0..4)
 * - Row 8: Bottom Apex (1 node at center col 2)
 *
 * Total = 1 + 5 + 5 + 5 + 5 + 5 + 5 + 5 + 1 = 37 intersections.
 */
class BoardTopology private constructor(
    val nodes: Map<Int, BoardNode>,
    val adjacency: Map<Int, Set<Int>>,
    val jumpPaths: List<JumpPath>
) {
    val jumpMapFromNode: Map<Int, List<JumpPath>> = jumpPaths.groupBy { it.fromNodeId }

    companion object {
        /**
         * Creates the traditional 37-node Sholo Guti topology matching the reference screenshot:
         * - Row 0: Top Triangle Base (3 nodes, x = 0.24, 0.50, 0.76, y = 0.08)
         * - Row 1: Top Triangle Middle (3 nodes, x = 0.37, 0.50, 0.63, y = 0.165)
         * - Row 2: Grid Row 0 (5 nodes, cols 0..4, y = 0.25; node 2 is top apex)
         * - Row 3: Grid Row 1 (5 nodes, cols 0..4, y = 0.375)
         * - Row 4: Grid Row 2 (5 nodes, cols 0..4, y = 0.50 - center neutral row)
         * - Row 5: Grid Row 3 (5 nodes, cols 0..4, y = 0.625)
         * - Row 6: Grid Row 4 (5 nodes, cols 0..4, y = 0.75; node 2 is bottom apex)
         * - Row 7: Bottom Triangle Middle (3 nodes, x = 0.37, 0.50, 0.63, y = 0.835)
         * - Row 8: Bottom Triangle Base (3 nodes, x = 0.24, 0.50, 0.76, y = 0.92)
         *
         * Total nodes = 3 + 3 + 25 + 3 + 3 = 37 intersections.
         * Player Two pieces = Rows 0..3 (3 + 3 + 5 + 5 = 16 Orange beads)
         * Neutral empty = Row 4 (5 spots)
         * Player One pieces = Rows 5..8 (5 + 5 + 3 + 3 = 16 Green beads)
         */
        fun createStandard37(): BoardTopology {
            val nodes = mutableMapOf<Int, BoardNode>()
            var idCounter = 0

            val gridX = floatArrayOf(0.08f, 0.29f, 0.50f, 0.71f, 0.92f)
            val gridY = floatArrayOf(0.25f, 0.375f, 0.50f, 0.625f, 0.75f)

            // 1. Row 0: Top Triangle Base (3 nodes)
            val r0Xs = floatArrayOf(0.24f, 0.50f, 0.76f)
            val r0Ids = IntArray(3)
            for (c in 0..2) {
                val id = idCounter++
                r0Ids[c] = id
                nodes[id] = BoardNode(id = id, row = 0, col = c + 1, x = r0Xs[c], y = 0.08f, name = "T0_$c")
            }

            // 2. Row 1: Top Triangle Middle (3 nodes)
            val r1Xs = floatArrayOf(0.37f, 0.50f, 0.63f)
            val r1Ids = IntArray(3)
            for (c in 0..2) {
                val id = idCounter++
                r1Ids[c] = id
                nodes[id] = BoardNode(id = id, row = 1, col = c + 1, x = r1Xs[c], y = 0.165f, name = "T1_$c")
            }

            // 3. Rows 2..6: Central 5x5 Grid (25 nodes)
            val gridIds = Array(5) { IntArray(5) }
            for (r in 0..4) {
                val boardRow = r + 2
                for (c in 0..4) {
                    val id = idCounter++
                    gridIds[r][c] = id
                    nodes[id] = BoardNode(
                        id = id,
                        row = boardRow,
                        col = c,
                        x = gridX[c],
                        y = gridY[r],
                        name = "G_${r}_$c"
                    )
                }
            }

            // 4. Row 7: Bottom Triangle Middle (3 nodes)
            val r7Xs = floatArrayOf(0.37f, 0.50f, 0.63f)
            val r7Ids = IntArray(3)
            for (c in 0..2) {
                val id = idCounter++
                r7Ids[c] = id
                nodes[id] = BoardNode(id = id, row = 7, col = c + 1, x = r7Xs[c], y = 0.835f, name = "B1_$c")
            }

            // 5. Row 8: Bottom Triangle Base (3 nodes)
            val r8Xs = floatArrayOf(0.24f, 0.50f, 0.76f)
            val r8Ids = IntArray(3)
            for (c in 0..2) {
                val id = idCounter++
                r8Ids[c] = id
                nodes[id] = BoardNode(id = id, row = 8, col = c + 1, x = r8Xs[c], y = 0.92f, name = "B0_$c")
            }

            // Graph Adjacency Construction
            val adj = mutableMapOf<Int, MutableSet<Int>>()
            for (id in 0 until idCounter) {
                adj[id] = mutableSetOf()
            }

            fun addEdge(u: Int, v: Int) {
                adj[u]?.add(v)
                adj[v]?.add(u)
            }

            // --- Top Triangle Connections ---
            // Horizontal bar in Row 0
            addEdge(r0Ids[0], r0Ids[1])
            addEdge(r0Ids[1], r0Ids[2])

            // Horizontal bar in Row 1
            addEdge(r1Ids[0], r1Ids[1])
            addEdge(r1Ids[1], r1Ids[2])

            // Radial lines from Top Grid Apex (gridIds[0][2]):
            // Left diagonal: Top Apex -> Row 1 Left -> Row 0 Left
            addEdge(gridIds[0][2], r1Ids[0])
            addEdge(r1Ids[0], r0Ids[0])

            // Center vertical: Top Apex -> Row 1 Center -> Row 0 Center
            addEdge(gridIds[0][2], r1Ids[1])
            addEdge(r1Ids[1], r0Ids[1])

            // Right diagonal: Top Apex -> Row 1 Right -> Row 0 Right
            addEdge(gridIds[0][2], r1Ids[2])
            addEdge(r1Ids[2], r0Ids[2])

            // --- Central 5x5 Grid Connections ---
            for (r in 0..4) {
                for (c in 0..4) {
                    val current = gridIds[r][c]

                    // Horizontal step
                    if (c + 1 <= 4) {
                        addEdge(current, gridIds[r][c + 1])
                    }

                    // Vertical step
                    if (r + 1 <= 4) {
                        addEdge(current, gridIds[r + 1][c])
                    }

                    // Diagonals (all intersections where (r + c) is even have diagonals)
                    if ((r + c) % 2 == 0) {
                        if (r + 1 <= 4 && c + 1 <= 4) {
                            addEdge(current, gridIds[r + 1][c + 1])
                        }
                        if (r + 1 <= 4 && c - 1 >= 0) {
                            addEdge(current, gridIds[r + 1][c - 1])
                        }
                    }
                }
            }

            // --- Bottom Triangle Connections ---
            // Horizontal bar in Row 7
            addEdge(r7Ids[0], r7Ids[1])
            addEdge(r7Ids[1], r7Ids[2])

            // Horizontal bar in Row 8
            addEdge(r8Ids[0], r8Ids[1])
            addEdge(r8Ids[1], r8Ids[2])

            // Radial lines from Bottom Grid Apex (gridIds[4][2]):
            // Left diagonal: Bottom Apex -> Row 7 Left -> Row 8 Left
            addEdge(gridIds[4][2], r7Ids[0])
            addEdge(r7Ids[0], r8Ids[0])

            // Center vertical: Bottom Apex -> Row 7 Center -> Row 8 Center
            addEdge(gridIds[4][2], r7Ids[1])
            addEdge(r7Ids[1], r8Ids[1])

            // Right diagonal: Bottom Apex -> Row 7 Right -> Row 8 Right
            addEdge(gridIds[4][2], r7Ids[2])
            addEdge(r7Ids[2], r8Ids[2])

            // --- Collinear Jump Captures ---
            val jumps = mutableListOf<JumpPath>()

            for ((aId, aNode) in nodes) {
                val neighborsOfA = adj[aId] ?: emptySet()
                for (bId in neighborsOfA) {
                    val bNode = nodes[bId] ?: continue
                    val dx1 = bNode.x - aNode.x
                    val dy1 = bNode.y - aNode.y

                    val neighborsOfB = adj[bId] ?: emptySet()
                    for (cId in neighborsOfB) {
                        if (cId == aId) continue
                        val cNode = nodes[cId] ?: continue
                        val dx2 = cNode.x - bNode.x
                        val dy2 = cNode.y - bNode.y

                        // Check collinearity: cross product ~ 0 and dot product > 0
                        val cross = dx1 * dy2 - dy1 * dx2
                        val dot = dx1 * dx2 + dy1 * dy2

                        if (kotlin.math.abs(cross) < 0.025f && dot > 0.0001f) {
                            jumps.add(JumpPath(fromNodeId = aId, overNodeId = bId, toNodeId = cId))
                        }
                    }
                }
            }

            return BoardTopology(nodes, adj, jumps.distinct())
        }
    }
}
