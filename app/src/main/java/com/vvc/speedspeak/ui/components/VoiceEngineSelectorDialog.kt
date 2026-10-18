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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vvc.speedspeak.model.TtsEngineType
import com.vvc.speedspeak.model.TtsVoice
import com.vvc.speedspeak.ui.theme.NeonCobalt
import com.vvc.speedspeak.ui.theme.NeonSuccess
import com.vvc.speedspeak.ui.theme.NeonViolet
import com.vvc.speedspeak.ui.theme.NeonVioletLight
import com.vvc.speedspeak.ui.theme.ObsidianCard
import com.vvc.speedspeak.ui.theme.ObsidianCardBorder
import com.vvc.speedspeak.ui.theme.TextMuted
import com.vvc.speedspeak.ui.theme.TextWhite

@Composable
fun VoiceEngineSelectorDialog(
    currentEngine: TtsEngineType,
    activeVoice: TtsVoice,
    kittenVoices: List<TtsVoice>,
    kokoroVoices: List<TtsVoice>,
    nativeVoices: List<TtsVoice>,
    speechPitch: Float,
    onSelectEngine: (TtsEngineType) -> Unit,
    onSelectVoice: (TtsVoice) -> Unit,
    onPitchChanged: (Float) -> Unit,
    onExportAudio: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(NeonViolet, NeonCobalt)
                    ),
                    RoundedCornerShape(24.dp)
                ),
            color = Color(0xFF0F0F1E)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Ajustes de Voz",
                            tint = NeonCobalt,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MOTORES Y VOCES OFFLINE",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Engine Selector Tabs (KittenTTS vs Kokoro vs Native)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF16162B))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isKitten = currentEngine == TtsEngineType.KITTEN_TTS
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isKitten) Brush.horizontalGradient(listOf(NeonViolet, NeonCobalt))
                                else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                            )
                            .clickable { onSelectEngine(TtsEngineType.KITTEN_TTS) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "KittenTTS",
                                color = if (isKitten) Color.Black else TextWhite,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "★ PRINCIPAL",
                                color = if (isKitten) Color(0xFF0F172A) else NeonCobalt,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    val isKokoro = currentEngine == TtsEngineType.KOKORO_82M
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isKokoro) NeonVioletDark else Color.Transparent)
                            .clickable { onSelectEngine(TtsEngineType.KOKORO_82M) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Kokoro-82M",
                                color = if (isKokoro) TextWhite else TextWhite.copy(alpha = 0.8f),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "RESPALDO",
                                color = if (isKokoro) NeonVioletLight else TextMuted,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    val isNative = currentEngine == TtsEngineType.NATIVE_OFFLINE
                    Box(
                        modifier = Modifier
                            .weight(0.9f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isNative) Color(0xFF0F3D5E) else Color.Transparent)
                            .clickable { onSelectEngine(TtsEngineType.NATIVE_OFFLINE) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Nativo",
                                color = if (isNative) NeonCobalt else TextMuted,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "SISTEMA",
                                color = if (isNative) NeonCobalt else TextMuted.copy(alpha = 0.7f),
                                fontSize = 8.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active engine info banner & resilience notice
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (currentEngine == TtsEngineType.KITTEN_TTS) Color(0xFF13192E)
                            else Color(0xFF1D142E)
                        )
                        .border(
                            1.dp,
                            if (currentEngine == TtsEngineType.KITTEN_TTS) NeonCobalt.copy(alpha = 0.4f)
                            else NeonViolet.copy(alpha = 0.4f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentEngine == TtsEngineType.KITTEN_TTS) Icons.Default.AutoAwesome else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (currentEngine == TtsEngineType.KITTEN_TTS) NeonCobalt else NeonVioletLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentEngine.description,
                                color = TextWhite,
                                fontSize = 11.sp,
                                lineHeight = 14.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (currentEngine == TtsEngineType.KITTEN_TTS) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✓ Sistema de respaldo automático: Si KittenTTS encuentra un fallo, conmuta a Kokoro TTS.",
                                color = NeonSuccess,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pitch adjustment slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tono de Voz (Pitch):",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${kotlin.math.round(speechPitch * 10) / 10f}x",
                        color = NeonCobalt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = speechPitch,
                    onValueChange = onPitchChanged,
                    valueRange = 0.5f..1.5f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCobalt,
                        activeTrackColor = NeonCobalt,
                        inactiveTrackColor = ObsidianCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (currentEngine) {
                        TtsEngineType.KITTEN_TTS -> "VOCES KITTENTTS (KITTENML 100% OFFLINE):"
                        TtsEngineType.KOKORO_82M -> "VOCES KOKORO TTS (82M RESPALDO):"
                        TtsEngineType.NATIVE_OFFLINE -> "VOCES INSTALADAS EN EL DISPOSITIVO:"
                    },
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Voice list
                val voices = when (currentEngine) {
                    TtsEngineType.KITTEN_TTS -> kittenVoices
                    TtsEngineType.KOKORO_82M -> kokoroVoices
                    TtsEngineType.NATIVE_OFFLINE -> nativeVoices
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(voices) { voice ->
                        val isSelected = voice.id == activeVoice.id
                        Row(
                            modifier = Modifier
                                .testTag("voice_item_${voice.id}")
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF26133B) else ObsidianCard)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonViolet else ObsidianCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectVoice(voice) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = voice.accentFlag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = voice.displayName,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${voice.gender} • ${voice.description}",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Seleccionada",
                                    tint = NeonVioletLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Export audio button
                Button(
                    onClick = {
                        onExportAudio()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B38))
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Exportar Audio",
                        tint = NeonCobalt,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Exportar Audio WAV (Motor Activo)",
                        color = NeonCobalt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private val NeonVioletDark = Color(0xFF4C1D95)
