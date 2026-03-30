package cam.lucane.studio.log.rpg.ui.screen.mj

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.screen.mj.components.*
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.MJViewModel

@Composable
fun MJScreen(
    viewModel: MJViewModel,
    autoStart: Boolean = false,
    onNavigateBack: () -> Unit
) {
    val isRunning by viewModel.isRunning.collectAsState()
    val players   by viewModel.players.collectAsState()
    val config    by viewModel.sessionConfig.collectAsState()

    var showQR         by remember { mutableStateOf(autoStart) }
    var showStopDialog by remember { mutableStateOf(false) }

    LaunchedEffect(autoStart) {
        if (autoStart && !isRunning) {
            viewModel.startSession()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
            .systemBarsPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            MJSessionBanner(
                isRunning   = isRunning,
                config      = config,
                playerCount = players.size,
                showQr      = showQR,
                onShowQR    = { showQR = !showQR }
            )
        }

        item {
            AnimatedVisibility(
                visible = showQR && config != null,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                config?.let { MJQRPanel(it) }
            }
        }

        if (players.isNotEmpty()) {
            item {
                Text(
                    text     = "JOUEURS CONNECTÉS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color    = ColorsSystem.TextDisabled,
                    letterSpacing = 1.5.sp,
                    fontFamily = NunitoFontFamily,
                    modifier = Modifier.padding(start = 2.dp, top = 4.dp)
                )
            }
            items(players, key = { it.socketId }) { slot ->
                MJPlayerCard(
                    slot   = slot,
                    onKick = { viewModel.kickPlayer(slot.socketId) }
                )
            }
        } else {
            item { MJWaitingForPlayers() }
        }

        item {
            Spacer(Modifier.height(4.dp))
            OutlinedButton(
                onClick  = { showStopDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                border   = androidx.compose.foundation.BorderStroke(1.5.dp, AccentRed.copy(alpha = 0.4f))
            ) {
                Text(
                    text       = "⏹ Terminer la session",
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily
                )
            }
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = {
                Text(
                    text       = "Terminer la session ?",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color      = ColorsSystem.TextPrimary
                )
            },
            text = {
                Text(
                    text       = "Tous les joueurs seront déconnectés.",
                    fontFamily = NunitoFontFamily,
                    color      = ColorsSystem.TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.stopSession()
                    showStopDialog = false
                    onNavigateBack()
                }) {
                    Text("Terminer", color = AccentRed, fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) {
                    Text("Annuler", fontFamily = NunitoFontFamily)
                }
            },
            containerColor = ColorsSystem.BackgroundCard
        )
    }
}