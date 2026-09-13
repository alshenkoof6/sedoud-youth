package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChurchNavy
import com.example.ui.theme.ChurchSecondary
import kotlin.math.abs

@Composable
fun QRCodeDisplay(
    token: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 220
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderPulse"
    )

    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(
                width = 3.dp,
                color = ChurchSecondary.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(sizeDp.dp)
                .background(Color.White)
        ) {
            val canvasSize = size.width
            val gridSize = 21 // Standard Version 1 QR code size (21x21)
            val moduleSize = canvasSize / gridSize
            val darkColor = ChurchNavy

            // Helper to draw a module
            fun drawModule(col: Int, row: Int) {
                drawRoundRect(
                    color = darkColor,
                    topLeft = Offset(col * moduleSize, row * moduleSize),
                    size = Size(moduleSize, moduleSize),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }

            // Draw standard QR 7x7 Finder Pattern at (startCol, startRow)
            fun drawFinder(startCol: Int, startRow: Int) {
                for (r in 0 until 7) {
                    for (c in 0 until 7) {
                        val isOuter = r == 0 || r == 6 || c == 0 || c == 6
                        val isInner = r in 2..4 && c in 2..4
                        if (isOuter || isInner) {
                            drawModule(startCol + c, startRow + r)
                        }
                    }
                }
            }

            // 1. Top-Left Finder
            drawFinder(0, 0)
            // 2. Top-Right Finder
            drawFinder(gridSize - 7, 0)
            // 3. Bottom-Left Finder
            drawFinder(0, gridSize - 7)

            // Timing patterns
            for (i in 7 until gridSize - 7 step 2) {
                drawModule(i, 6) // Horizontal timing
                drawModule(6, i) // Vertical timing
            }

            // Pseudo-random deterministic module distribution based on token hash
            val hash = abs(token.hashCode())
            var seed = hash
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    // Skip finders and separators
                    val inTopLeft = r < 8 && c < 8
                    val inTopRight = r < 8 && c >= gridSize - 8
                    val inBottomLeft = r >= gridSize - 8 && c < 8
                    val inTiming = (r == 6 || c == 6)

                    if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                        seed = (seed * 1103515245 + 12345) and 0x7FFFFFFF
                        if ((seed % 3) == 0 || ((r + c + (seed % 5)) % 2 == 0)) {
                            drawModule(c, r)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "رمز أمني قصير الأجل: ${token.takeLast(10)}",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
    }
}
