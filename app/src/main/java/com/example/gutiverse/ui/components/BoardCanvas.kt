package com.example.gutiverse.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.gutiverse.engine.BoardTopology
import com.example.gutiverse.engine.GameState
import com.example.gutiverse.engine.PlayerSide
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BoardCanvas(
    topology: BoardTopology,
    gameState: GameState,
    onNodeClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.55f) // Preserves exact 37-node Sholo Guti board proportions
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(topology) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height

                        var closestNodeId: Int? = null
                        var minDistance = Float.MAX_VALUE
                        val maxThreshold = 46.dp.toPx()

                        for ((id, node) in topology.nodes) {
                            val nodeX = node.x * w
                            val nodeY = node.y * h
                            val dist = kotlin.math.hypot(
                                (tapOffset.x - nodeX).toDouble(),
                                (tapOffset.y - nodeY).toDouble()
                            ).toFloat()
                            if (dist < minDistance && dist <= maxThreshold) {
                                minDistance = dist
                                closestNodeId = id
                            }
                        }

                        closestNodeId?.let { onNodeClicked(it) }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val beadRadius = w * 0.052f

            // 0. Semi-transparent cyber tint so the robotic machinery background shines through
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A1938).copy(alpha = 0.35f),
                        Color(0xFF070B1E).copy(alpha = 0.50f),
                        Color(0xFF050412).copy(alpha = 0.65f)
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = h * 0.65f
                ),
                topLeft = Offset.Zero,
                size = Size(w, h),
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
            )

            // Subtle robotic tech grid lines
            val gridStep = w / 10f
            val techGridColor = Color(0xFF00B0FF).copy(alpha = 0.07f)
            var gx = gridStep
            while (gx < w) {
                drawLine(color = techGridColor, start = Offset(gx, 0f), end = Offset(gx, h), strokeWidth = 1.dp.toPx())
                gx += gridStep
            }
            var gy = gridStep
            while (gy < h) {
                drawLine(color = techGridColor, start = Offset(0f, gy), end = Offset(w, gy), strokeWidth = 1.dp.toPx())
                gy += gridStep
            }

            // Robotic corner brackets
            val bracketLen = 22.dp.toPx()
            val bracketColor = Color(0xFF00E5FF).copy(alpha = 0.45f)
            val bStroke = 2.dp.toPx()
            // Top-left
            drawLine(bracketColor, Offset(4f, 4f), Offset(4f + bracketLen, 4f), bStroke)
            drawLine(bracketColor, Offset(4f, 4f), Offset(4f, 4f + bracketLen), bStroke)
            // Top-right
            drawLine(bracketColor, Offset(w - 4f, 4f), Offset(w - 4f - bracketLen, 4f), bStroke)
            drawLine(bracketColor, Offset(w - 4f, 4f), Offset(w - 4f, 4f + bracketLen), bStroke)
            // Bottom-left
            drawLine(bracketColor, Offset(4f, h - 4f), Offset(4f + bracketLen, h - 4f), bStroke)
            drawLine(bracketColor, Offset(4f, h - 4f), Offset(4f, h - 4f - bracketLen), bStroke)
            // Bottom-right
            drawLine(bracketColor, Offset(w - 4f, h - 4f), Offset(w - 4f - bracketLen, h - 4f), bStroke)
            drawLine(bracketColor, Offset(w - 4f, h - 4f), Offset(w - 4f, h - 4f - bracketLen), bStroke)

            // 1. Draw Neon Blue Lighting Connecting Lines
            val drawnEdges = mutableSetOf<Pair<Int, Int>>()
            for ((u, neighbors) in topology.adjacency) {
                val nodeU = topology.nodes[u] ?: continue
                val p1 = Offset(nodeU.x * w, nodeU.y * h)

                for (v in neighbors) {
                    val key = if (u < v) Pair(u, v) else Pair(v, u)
                    if (drawnEdges.add(key)) {
                        val nodeV = topology.nodes[v] ?: continue
                        val p2 = Offset(nodeV.x * w, nodeV.y * h)

                        // Outer soft blue neon glow
                        drawLine(
                            color = Color(0xFF0091EA).copy(alpha = 0.35f + 0.15f * pulseProgress),
                            start = p1,
                            end = p2,
                            strokeWidth = 8.5.dp.toPx()
                        )
                        // Mid electric blue glow
                        drawLine(
                            color = Color(0xFF00E5FF).copy(alpha = 0.75f),
                            start = p1,
                            end = p2,
                            strokeWidth = 4.0.dp.toPx()
                        )
                        // Bright ice-blue core
                        drawLine(
                            color = Color(0xFFE0F7FA),
                            start = p1,
                            end = p2,
                            strokeWidth = 1.6.dp.toPx()
                        )
                    }
                }
            }

            // 2. Draw Glowing Blue Intersection Dots at empty nodes
            for ((id, node) in topology.nodes) {
                val center = Offset(node.x * w, node.y * h)
                val piece = gameState.board[id] ?: PlayerSide.NONE

                if (piece == PlayerSide.NONE) {
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = 0.45f),
                        radius = 7.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFFE0F7FA),
                        radius = 4.2.dp.toPx(),
                        center = center
                    )
                }
            }

            // 3. Highlight Legal Move Targets
            for (targetId in gameState.legalTargetIds) {
                val node = topology.nodes[targetId] ?: continue
                val center = Offset(node.x * w, node.y * h)
                val targetRadius = beadRadius * (0.85f + 0.20f * pulseProgress)

                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.35f + 0.25f * pulseProgress),
                    radius = targetRadius,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF00E5FF),
                    radius = targetRadius,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawCircle(
                    color = Color.White,
                    radius = beadRadius * 0.25f,
                    center = center
                )
            }

            // 4. Draw Updated Cyber-Luxe 3D Star Beads
            // Player Two (Top): Neon Ruby Crimson | Player One (Bottom): Luxury Cyber Gold
            for ((nodeId, side) in gameState.board) {
                if (side == PlayerSide.NONE) continue

                val node = topology.nodes[nodeId] ?: continue
                val center = Offset(node.x * w, node.y * h)
                val isSelected = gameState.selectedNodeId == nodeId
                val isJumpingChain = gameState.activeJumpingNodeId == nodeId

                if (isSelected || isJumpingChain) {
                    val haloColor = if (isJumpingChain) Color(0xFFFF1744) else Color(0xFF00E5FF)
                    val haloRadius = beadRadius * (1.30f + 0.20f * pulseProgress)

                    drawCircle(
                        color = haloColor.copy(alpha = 0.45f * pulseProgress),
                        radius = haloRadius,
                        center = center
                    )
                    drawCircle(
                        color = haloColor,
                        radius = haloRadius,
                        center = center,
                        style = Stroke(width = 2.8.dp.toPx())
                    )
                }

                // Drop shadow underneath each bead
                drawCircle(
                    color = Color(0x99000000),
                    radius = beadRadius * 1.05f,
                    center = center + Offset(0f, 2.5.dp.toPx())
                )

                if (side == PlayerSide.PLAYER_TWO) {
                    // --- PLAYER TWO: 16 NEON RUBY CRIMSON TOKENS ---
                    drawCircle(
                        color = Color(0xFF8E0022),
                        radius = beadRadius,
                        center = center
                    )

                    val crimsonBodyBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF8A80), // Top highlight
                            Color(0xFFFF1744), // Vibrant ruby crimson
                            Color(0xFFD50000)  // Deep crimson edge
                        ),
                        center = center - Offset(beadRadius * 0.25f, beadRadius * 0.25f),
                        radius = beadRadius * 1.1f
                    )
                    drawCircle(
                        brush = crimsonBodyBrush,
                        radius = beadRadius * 0.90f,
                        center = center
                    )

                    drawCircle(
                        color = Color(0xFFFF5252),
                        radius = beadRadius * 0.70f,
                        center = center,
                        style = Stroke(width = 1.3.dp.toPx())
                    )

                    drawStar(
                        center = center,
                        outerRadius = beadRadius * 0.45f,
                        innerRadius = beadRadius * 0.22f,
                        points = 6,
                        color = Color(0xFF6A0014)
                    )

                    drawCircle(
                        color = Color.White.copy(alpha = 0.40f),
                        radius = beadRadius * 0.16f,
                        center = center - Offset(beadRadius * 0.25f, beadRadius * 0.25f)
                    )
                } else if (side == PlayerSide.PLAYER_ONE) {
                    // --- PLAYER ONE: 16 LUXURY CYBER GOLD TOKENS ---
                    drawCircle(
                        color = Color(0xFF9E6B00),
                        radius = beadRadius,
                        center = center
                    )

                    val goldBodyBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF9C4), // Top highlight
                            Color(0xFFFFD600), // Rich cyber gold
                            Color(0xFFFFAB00)  // Deep amber gold edge
                        ),
                        center = center - Offset(beadRadius * 0.25f, beadRadius * 0.25f),
                        radius = beadRadius * 1.1f
                    )
                    drawCircle(
                        brush = goldBodyBrush,
                        radius = beadRadius * 0.90f,
                        center = center
                    )

                    drawCircle(
                        color = Color(0xFFB28704),
                        radius = beadRadius * 0.70f,
                        center = center,
                        style = Stroke(width = 1.3.dp.toPx())
                    )

                    drawStar(
                        center = center,
                        outerRadius = beadRadius * 0.45f,
                        innerRadius = beadRadius * 0.22f,
                        points = 6,
                        color = Color(0xFF6D4C00)
                    )

                    drawCircle(
                        color = Color.White.copy(alpha = 0.45f),
                        radius = beadRadius * 0.16f,
                        center = center - Offset(beadRadius * 0.25f, beadRadius * 0.25f)
                    )
                }
            }
        }
    }
}

/**
 * Draws a geometric star with [points] rays centered at [center].
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    points: Int = 6,
    color: Color
) {
    val totalVertices = points * 2
    val step = (2.0 * Math.PI / totalVertices).toFloat()
    val startAngle = (-Math.PI / 2.0).toFloat()

    val path = Path()
    for (i in 0 until totalVertices) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val angle = startAngle + i * step
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    drawPath(path = path, color = color)
}
