package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.TtsEngineType
import com.example.ui.theme.NeonCobalt
import com.example.ui.theme.NeonSuccess
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.NeonVioletLight
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FuturisticHeader(
    engineType: TtsEngineType,
    onOpenVoiceSettings: () -> Unit,
    onOpenCiCdInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141428),
                        Color(0xFF090912)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonViolet.copy(alpha = 0.6f),
                        NeonCobalt.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Top branding row with logo, title, and actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Official VVC Emblem
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Brush.linearGradient(listOf(NeonViolet, NeonCobalt)), CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF07070D)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Logo VVC Speed Speak",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "VVC SPEED SPEAK",
                        color = TextWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.1.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "Texto → Voz → Audio",
                        color = NeonCobalt,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Top action buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenCiCdInfo,
                    modifier = Modifier
                        .testTag("cicd_info_button")
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Guía de compilación y CI/CD",
                        tint = TextMuted
                    )
                }

                IconButton(
                    onClick = onOpenVoiceSettings,
                    modifier = Modifier
                        .testTag("header_voice_settings_button")
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = "Configurar Voz",
                        tint = NeonVioletLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Official Hero Branding Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(NeonViolet.copy(alpha = 0.5f), NeonCobalt.copy(alpha = 0.5f))
                    ),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.vvc_branding_banner),
                contentDescription = "Banner Oficial VVC Speed Speak",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 7.2f),
                contentScale = ContentScale.Crop
            )

            // Cybernetic subtle bottom overlay with slogan
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC07070D))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "“TU TEXTO MERECE UNA VOZ REAL”",
                        color = Color(0xFFE2E8F0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.9.sp
                    )
                    Text(
                        text = "KITTENTTS • KOKORO",
                        color = NeonCobalt,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Official Branding Feature Pills from banner:
        // [ ⚡ RÁPIDO ] [ 🧠 IA NATURAL ] [ 🔒 PRIVADO ] [ 🛡️ EN TU DISPOSITIVO ]
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BrandingPill(icon = Icons.Default.Bolt, text = "RÁPIDO", tint = NeonCobalt)
            BrandingPill(icon = Icons.Default.Psychology, text = "IA NATURAL", tint = NeonVioletLight)
            BrandingPill(icon = Icons.Default.Lock, text = "PRIVADO", tint = NeonSuccess)
            BrandingPill(icon = Icons.Default.Security, text = "EN TU DISPOSITIVO", tint = Color(0xFF38BDF8))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // System status row: Offline + Active Engine
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 100% Offline Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF06231A))
                    .border(1.dp, NeonSuccess.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "100% Offline",
                    tint = NeonSuccess,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "100% OFFLINE",
                    color = NeonSuccess,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Engine Badge
            val badgeBg = when (engineType) {
                TtsEngineType.KITTEN_TTS -> Color(0xFF0C2238)
                TtsEngineType.KOKORO_82M -> Color(0xFF24133A)
                TtsEngineType.NATIVE_OFFLINE -> ObsidianCard
            }
            val badgeBorder = when (engineType) {
                TtsEngineType.KITTEN_TTS -> NeonCobalt.copy(alpha = 0.8f)
                TtsEngineType.KOKORO_82M -> NeonViolet.copy(alpha = 0.8f)
                TtsEngineType.NATIVE_OFFLINE -> ObsidianCardBorder
            }
            val dotColor = when (engineType) {
                TtsEngineType.KITTEN_TTS -> NeonCobalt
                TtsEngineType.KOKORO_82M -> NeonViolet
                TtsEngineType.NATIVE_OFFLINE -> Color(0xFF94A3B8)
            }
            val textColor = when (engineType) {
                TtsEngineType.KITTEN_TTS -> NeonCobalt
                TtsEngineType.KOKORO_82M -> NeonVioletLight
                TtsEngineType.NATIVE_OFFLINE -> TextMuted
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .border(1.dp, badgeBorder, RoundedCornerShape(8.dp))
                    .clickable { onOpenVoiceSettings() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = engineType.badge,
                    color = textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun BrandingPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF101020))
            .border(1.dp, Color(0xFF22223C), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = Color(0xFFCBD5E1),
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
    }
}
