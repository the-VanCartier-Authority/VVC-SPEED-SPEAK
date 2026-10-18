package com.vvc.speedspeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vvc.speedspeak.ui.MainScreen
import com.vvc.speedspeak.ui.theme.VvcSpeedSpeakTheme
import com.vvc.speedspeak.ui.theme.ObsidianBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VvcSpeedSpeakTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBackground
                ) {
                    MainScreen()
                }
            }
        }
    }
}
