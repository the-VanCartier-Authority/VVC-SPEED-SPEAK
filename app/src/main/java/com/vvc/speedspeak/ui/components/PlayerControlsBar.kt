package com.vvc.speedspeak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vvc.speedspeak.model.PlaybackStatus
import com.vvc.speedspeak.model.TtsVoice
import com.vvc.speedspeak.ui.theme.NeonCobalt
import com.vvc.speedspeak.ui.theme.NeonViolet
import com.vvc.speedspeak.ui.theme.NeonVioletLight
import com.vvc.speedspeak.ui.theme.ObsidianCard
import com.vvc.speedspeak.ui.theme.ObsidianCardBorder
import com.vvc.speedspeak.ui.theme.TextMuted
import com.vvc.speedspeak.ui.theme.TextWhite

@Composable
fun PlayerControlsBar(
    playbackStatus: PlaybackStatus,
    currentSentenceIndex: Int,
    totalSentences: Int,
    speechRate: Float,
    activeVoice: TtsVoice,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying = playbackStatus == PlaybackStatus.PLAYING
    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 2.5f, 3.0f)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF141428),
                        Color(0xFF090912)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        NeonViolet.copy(alpha = 0.6f),
                        NeonCobalt.copy(alpha = 0.3f)
                    )
                ),
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Voice preview & Progress info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Active Voice Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1F1B38))
                    .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onVoiceClick() }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = activeVoice.accentFlag, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Voz",
                    tint = NeonVioletLight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = activeVoice.displayName,
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Progress text
            val percent = if (totalSentences > 0) {
                ((currentSentenceIndex.toFloat() / totalSentences.toFloat()) * 100).toInt()
            } else 0
            Text(
                text = "$percent% • ${currentSentenceIndex + 1}/$totalSentences",
                color = NeonCobalt,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Seekbar
        if (totalSentences > 1) {
            Slider(
                value = currentSentenceIndex.toFloat(),
                onValueChange = { onSeek(it.toInt()) },
                valueRange = 0f..(totalSentences - 1).toFloat(),
                modifier = Modifier
                    .testTag("player_seek_slider")
                    .fillMaxWidth()
                    .height(26.dp),
                colors = SliderDefaults.colors(
                    thumbColor = NeonCobalt,
                    activeTrackColor = NeonViolet,
                    inactiveTrackColor = ObsidianCardBorder
                )
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Main Transport Controls (Prev, Play/Pause, Stop, Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous sentence
            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier
                    .testTag("player_skip_prev_button")
                    .size(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Frase Anterior",
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Play / Pause Neon Action Button
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .shadow(16.dp, CircleShape, ambientColor = NeonViolet, spotColor = NeonCobalt)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                NeonViolet,
                                NeonCobalt
                            )
                        )
                    )
                    .clickable {
                        if (isPlaying) onPause() else onPlay()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = Color.Black,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Stop
            IconButton(
                onClick = onStop,
                modifier = Modifier
                    .testTag("player_stop_button")
                    .size(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Detener",
                    tint = if (isPlaying) Color(0xFFEF4444) else TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Next sentence
            IconButton(
                onClick = onSkipNext,
                modifier = Modifier
                    .testTag("player_skip_next_button")
                    .size(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Frase Siguiente",
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Speed Selector row with quick chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = "Velocidad",
                tint = NeonCobalt,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "VELOCIDAD:",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                speedOptions.forEach { rate ->
                    val isSelected = kotlin.math.abs(speechRate - rate) < 0.05f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NeonCobalt else ObsidianCard)
                            .border(
                                1.dp,
                                if (isSelected) NeonCobalt else ObsidianCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSpeedSelected(rate) }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${rate}x",
                            color = if (isSelected) Color.Black else TextWhite,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
