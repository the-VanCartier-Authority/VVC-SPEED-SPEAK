package com.vvc.speedspeak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vvc.speedspeak.ui.theme.NeonCobalt
import com.vvc.speedspeak.ui.theme.NeonSuccess
import com.vvc.speedspeak.ui.theme.NeonViolet
import com.vvc.speedspeak.ui.theme.NeonVioletLight
import com.vvc.speedspeak.ui.theme.TextMuted
import com.vvc.speedspeak.ui.theme.TextWhite

@Composable
fun CiCdGuideDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NeonCobalt.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = Color(0xFF0F0F1E)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "CI/CD",
                            tint = NeonCobalt,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COMPILACIÓN AUTOMÁTICA EN LA NUBE",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
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

                Text(
                    text = "El repositorio incluye todos los archivos configurados para compilar automáticamente el APK firmado en GitHub Actions:",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Item 1: GitHub Actions Workflows
                CiCdItem(
                    icon = Icons.Default.Code,
                    title = ".github/workflows/main.yml",
                    subtitle = "Workflow automatizado que compila APK Release/Debug con Java 17, Gradle 8+ y empaqueta los artefactos."
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Item 2: Buildozer Spec
                CiCdItem(
                    icon = Icons.Default.Code,
                    title = "buildozer.spec & main.py",
                    subtitle = "Configuración completa para Kivy / Buildozer con permisos de almacenamiento, audio y motor Kokoro TTS offline."
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Item 3: Keystore & Secrets
                CiCdItem(
                    icon = Icons.Default.Key,
                    title = "Variables de Entorno para Firma APK",
                    subtitle = "Configurado para usar GitHub Secrets: KEYSTORE_BASE64, STORE_PASSWORD, KEY_ALIAS y KEY_PASSWORD."
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Secrets box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF090914))
                        .border(1.dp, Color(0xFF1E1E36), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "GitHub Repository Secrets:",
                            color = NeonVioletLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• KEYSTORE_BASE64 (base64 de tu archivo .jks)\n• STORE_PASSWORD (contraseña almacén)\n• KEY_ALIAS (alias de la clave)\n• KEY_PASSWORD (contraseña de la clave)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CiCdItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF16162B))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonCobalt,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
