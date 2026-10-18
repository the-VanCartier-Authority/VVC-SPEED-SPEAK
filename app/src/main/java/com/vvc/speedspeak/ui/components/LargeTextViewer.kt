package com.vvc.speedspeak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vvc.speedspeak.model.ExtractedDocument
import com.vvc.speedspeak.model.PlaybackStatus
import com.vvc.speedspeak.ui.theme.NeonCobalt
import com.vvc.speedspeak.ui.theme.NeonViolet
import com.vvc.speedspeak.ui.theme.NeonVioletLight
import com.vvc.speedspeak.ui.theme.ObsidianCard
import com.vvc.speedspeak.ui.theme.ObsidianCardBorder
import com.vvc.speedspeak.ui.theme.TextMuted
import com.vvc.speedspeak.ui.theme.TextWhite

@Composable
fun LargeTextViewer(
    document: ExtractedDocument,
    currentSentenceIndex: Int,
    playbackStatus: PlaybackStatus,
    onSentenceClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Auto-scroll to active sentence when reading
    LaunchedEffect(currentSentenceIndex, playbackStatus) {
        if (playbackStatus == PlaybackStatus.PLAYING && currentSentenceIndex in document.sentences.indices) {
            val target = maxOf(0, currentSentenceIndex - 1)
            listState.animateScrollToItem(target)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ObsidianCard)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        NeonCobalt.copy(alpha = 0.5f),
                        NeonViolet.copy(alpha = 0.3f),
                        ObsidianCardBorder
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        // Viewer Header with Sentence Counter & Search Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (playbackStatus == PlaybackStatus.PLAYING) NeonViolet else NeonCobalt)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ÁREA DE VISUALIZACIÓN DE TEXTO",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (document.sentences.isNotEmpty()) {
                    Text(
                        text = "Frase ${if (document.sentences.isEmpty()) 0 else currentSentenceIndex + 1}/${document.sentences.size}",
                        color = NeonCobalt,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0D1B2A))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                IconButton(
                    onClick = { isSearchActive = !isSearchActive },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Buscar en texto",
                        tint = if (isSearchActive) NeonVioletLight else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (isSearchActive) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar palabra...", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCobalt,
                    unfocusedBorderColor = ObsidianCardBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = NeonCobalt
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main text content
        if (document.fullText.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090912))
                    .border(1.dp, Color(0xFF1B1B30), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ningún documento cargado",
                        color = TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Adjunta un PDF o TXT para comenzar la lectura offline.",
                        color = TextMuted.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .testTag("text_viewer_scrollable")
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090912))
                    .border(1.dp, Color(0xFF1B1B30), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(document.sentences) { index, sentence ->
                    val isCurrent = index == currentSentenceIndex
                    val isSearchMatch = searchQuery.isNotBlank() &&
                        sentence.contains(searchQuery, ignoreCase = true)

                    val backgroundColor = when {
                        isCurrent -> Color(0xFF281142)
                        isSearchMatch -> Color(0xFF0F2B3E)
                        else -> Color.Transparent
                    }

                    val borderColor = when {
                        isCurrent -> NeonViolet
                        isSearchMatch -> NeonCobalt
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .border(
                                width = if (isCurrent || isSearchMatch) 1.5.dp else 0.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onSentenceClick(index) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "${index + 1}.",
                                color = if (isCurrent) NeonCobalt else TextMuted.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(28.dp)
                            )
                            Text(
                                text = sentence,
                                color = if (isCurrent) TextWhite else if (isSearchMatch) NeonCobalt else TextMuted,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
