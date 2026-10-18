package com.vvc.speedspeak.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vvc.speedspeak.ui.theme.NeonCobalt
import com.vvc.speedspeak.ui.theme.NeonViolet

@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val barCount = 28
    val transition = rememberInfiniteTransition(label = "audio_anim")

    // Create 4 phase-shifted animations to drive the bars
    val anim1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase1"
    )

    val anim2 by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase2"
    )

    val anim3 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(580, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase3"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barGradients = listOf(
            Brush.verticalGradient(listOf(NeonViolet, NeonCobalt)),
            Brush.verticalGradient(listOf(NeonCobalt, NeonViolet)),
            Brush.verticalGradient(listOf(NeonViolet, Color(0xFFC084FC))),
            Brush.verticalGradient(listOf(NeonCobalt, Color(0xFF38BDF8)))
        )

        for (i in 0 until barCount) {
            val baseHeightFactor = when (i % 4) {
                0 -> anim1
                1 -> anim2
                2 -> anim3
                else -> (anim1 + anim2) / 2f
            }

            // Distribute natural wave bell curve towards center
            val centerWeight = 1f - kotlin.math.abs(i - barCount / 2f) / (barCount / 1.6f)
            val finalHeight = if (isPlaying) {
                maxOf(6f, 32f * baseHeightFactor * maxOf(0.4f, centerWeight))
            } else {
                5f // Idle baseline
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(finalHeight.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barGradients[i % barGradients.size])
            )
        }
    }
}
