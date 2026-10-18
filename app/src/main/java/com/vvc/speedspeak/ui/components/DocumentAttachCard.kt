package com.vvc.speedspeak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vvc.speedspeak.model.ExtractedDocument
import com.vvc.speedspeak.ui.theme.NeonCobalt
import com.vvc.speedspeak.ui.theme.NeonCobaltLight
import com.vvc.speedspeak.ui.theme.NeonViolet
import com.vvc.speedspeak.ui.theme.NeonVioletLight
import com.vvc.speedspeak.ui.theme.ObsidianCard
import com.vvc.speedspeak.ui.theme.ObsidianCardBorder
import com.vvc.speedspeak.ui.theme.TextMuted
import com.vvc.speedspeak.ui.theme.TextWhite
import com.vvc.speedspeak.util.DocumentExtractor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DocumentAttachCard(
    document: ExtractedDocument,
    isLoading: Boolean,
    onAttachClicked: () -> Unit,
    onPasteClicked: () -> Unit,
    onSelectSample: (String, String) -> Unit,
    onClearClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianCardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Prominent Attach Document Button with Cyberpunk Glowing Gradient
        Button(
            onClick = onAttachClicked,
            modifier = Modifier
                .testTag("attach_document_button")
                .fillMaxWidth()
                .height(54.dp)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(14.dp), ambientColor = NeonViolet, spotColor = NeonCobalt),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF8B2FC9),
                                Color(0xFF6B21A8),
                                Color(0xFF0284C7),
                                Color(0xFF00D4FF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = TextWhite,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Extrayendo texto offline...",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = "Adjuntar Documento",
                            tint = TextWhite,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ADJUNTAR DOCUMENTO (PDF / TXT)",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary quick actions (Paste text, Samples, Clear)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPasteClicked,
                modifier = Modifier
                    .testTag("paste_text_button")
                    .weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCobaltLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCobalt.copy(alpha = 0.4f))
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Pegar Texto",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Pegar Texto",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (document.fullText.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onClearClicked,
                    modifier = Modifier.testTag("clear_text_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Limpiar", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sample text pills
        Text(
            text = "O prueba textos pre-cargados:",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DocumentExtractor.sampleDocuments.forEach { (title, content) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF19192C))
                        .border(1.dp, ObsidianCardBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectSample(title, content) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = title,
                        color = NeonVioletLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Metadata chip if a document is present
        if (document.fullText.isNotBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0D0D18))
                    .border(1.dp, Color(0xFF23233D), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (document.fileType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                        contentDescription = "Tipo Archivo",
                        tint = if (document.fileType == "PDF") Color(0xFFF87171) else NeonCobalt,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = document.fileName.ifBlank { "Documento cargado" },
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatListNumbered,
                        contentDescription = "Palabras",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${document.wordCount} palabras",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Tiempo",
                        tint = NeonCobalt,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val mins = kotlin.math.round(document.estimatedDurationMinutes * 10) / 10.0
                    Text(
                        text = "~${mins}m",
                        color = NeonCobalt,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
