package com.example.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.PlaybackStatus
import com.example.ui.components.AudioVisualizer
import com.example.ui.components.CiCdGuideDialog
import com.example.ui.components.DocumentAttachCard
import com.example.ui.components.FuturisticHeader
import com.example.ui.components.LargeTextViewer
import com.example.ui.components.PasteTextDialog
import com.example.ui.components.PlayerControlsBar
import com.example.ui.components.VoiceEngineSelectorDialog
import com.example.ui.theme.ObsidianBackground
import com.example.viewmodel.SpeedSpeakViewModel

@Composable
fun MainScreen(
    viewModel: SpeedSpeakViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val document by viewModel.document.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val playbackStatus by viewModel.playbackStatus.collectAsState()
    val currentSentenceIndex by viewModel.currentSentenceIndex.collectAsState()
    val totalSentences by viewModel.totalSentences.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()
    val speechPitch by viewModel.speechPitch.collectAsState()
    val engineType by viewModel.engineType.collectAsState()
    val activeVoice by viewModel.activeVoice.collectAsState()
    val availableNativeVoices by viewModel.availableNativeVoices.collectAsState()
    val exportMessage by viewModel.exportMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val fallbackNotice by viewModel.fallbackTriggered.collectAsState()

    var showVoiceDialog by remember { mutableStateOf(false) }
    var showPasteDialog by remember { mutableStateOf(false) }
    var showCiCdDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(fallbackNotice) {
        fallbackNotice?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.dismissFallbackNotice()
        }
    }

    LaunchedEffect(exportMessage) {
        exportMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.dismissExportMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.dismissErrorMessage()
        }
    }

    // Document picker for local PDF and TXT
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.loadUri(uri)
        }
    }

    val isPlaying = playbackStatus == PlaybackStatus.PLAYING

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            PlayerControlsBar(
                playbackStatus = playbackStatus,
                currentSentenceIndex = currentSentenceIndex,
                totalSentences = totalSentences,
                speechRate = speechRate,
                activeVoice = activeVoice,
                onPlay = { viewModel.play() },
                onPause = { viewModel.pause() },
                onStop = { viewModel.stop() },
                onSkipPrevious = { viewModel.skipPrevious() },
                onSkipNext = { viewModel.skipNext() },
                onSeek = { viewModel.seekToSentence(it) },
                onSpeedSelected = { viewModel.setSpeed(it) },
                onVoiceClick = { showVoiceDialog = true },
                modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Safe insets padding top
            Spacer(modifier = Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))

            // Futuristic Header with VVC Speed Speak branding and badges
            FuturisticHeader(
                engineType = engineType,
                onOpenVoiceSettings = { showVoiceDialog = true },
                onOpenCiCdInfo = { showCiCdDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Audio Visualizer that pulses when speaking
                AudioVisualizer(
                    isPlaying = isPlaying,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Attach Document Card
                DocumentAttachCard(
                    document = document,
                    isLoading = isLoading,
                    onAttachClicked = {
                        documentLauncher.launch(
                            arrayOf("application/pdf", "text/plain", "text/*")
                        )
                    },
                    onPasteClicked = { showPasteDialog = true },
                    onSelectSample = { title, content ->
                        viewModel.loadSample(title, content)
                    },
                    onClearClicked = { viewModel.clearDocument() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Large Text Viewer Card with Karaoke sentence highlight
                LargeTextViewer(
                    document = document,
                    currentSentenceIndex = currentSentenceIndex,
                    playbackStatus = playbackStatus,
                    onSentenceClick = { viewModel.seekToSentence(it) }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Voice & Engine Settings Dialog
    if (showVoiceDialog) {
        VoiceEngineSelectorDialog(
            currentEngine = engineType,
            activeVoice = activeVoice,
            kittenVoices = viewModel.availableKittenVoices,
            kokoroVoices = viewModel.availableKokoroVoices,
            nativeVoices = availableNativeVoices,
            speechPitch = speechPitch,
            onSelectEngine = { viewModel.setEngineType(it) },
            onSelectVoice = { viewModel.setVoice(it) },
            onPitchChanged = { viewModel.setPitch(it) },
            onExportAudio = { viewModel.exportCurrentSpeechToAudio() },
            onDismiss = { showVoiceDialog = false }
        )
    }

    // Paste Text Dialog
    if (showPasteDialog) {
        PasteTextDialog(
            initialText = document.fullText,
            onConfirm = { viewModel.updateManualText(it) },
            onDismiss = { showPasteDialog = false }
        )
    }

    // CI/CD & Buildozer Guide Dialog
    if (showCiCdDialog) {
        CiCdGuideDialog(
            onDismiss = { showCiCdDialog = false }
        )
    }
}
