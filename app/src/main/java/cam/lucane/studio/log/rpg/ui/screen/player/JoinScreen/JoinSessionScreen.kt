package cam.lucane.studio.log.rpg.ui.screen.player

import androidx.compose.runtime.*
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen.ManualCodeContent
import cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen.PickCharacterContent
import cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen.PseudoContent

enum class JoinStep { SCAN, MANUAL, PSEUDO, PICK }

@Composable
fun JoinSessionScreen(
    characters      : List<Character>,
    playerName      : String,
    onNameChange    : (String) -> Unit,
    onQRScanned     : (String) -> Boolean,
    onCodeEntered   : (code: String, onResult: (Boolean) -> Unit) -> Unit,
    onConnect       : (onResult: (Boolean) -> Unit) -> Unit,
    onCharacterPicked: (Character) -> Unit,
    onCancel        : () -> Unit,
    startStep       : JoinStep = JoinStep.SCAN,
) {
    var step         by remember { mutableStateOf(startStep) }
    var connecting   by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    when (step) {

        // ── Scanner QR ────────────────────────────────────────────────────────
        JoinStep.SCAN -> QRScannerScreen(
            onQRScanned = { content ->
                val ok = onQRScanned(content)
                if (ok) step = JoinStep.PSEUDO
                else errorMessage = "QR Code invalide"
            },
            onEnterCodeManually = { step = JoinStep.MANUAL },
            onCancel = onCancel,
        )

        // ── Saisie manuelle du code ────────────────────────────────────────────
        JoinStep.MANUAL -> ManualCodeContent(
            onCodeConfirmed = { code, onResult ->
                onCodeEntered(code) { ok ->
                    if (ok) {
                        step = JoinStep.PSEUDO
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
            },
            onBack = { step = JoinStep.SCAN },
        )

        // ── Saisie du pseudo ──────────────────────────────────────────────────
        JoinStep.PSEUDO -> PseudoContent(
            playerName   = playerName,
            onNameChange = onNameChange,
            connecting   = connecting,
            errorMessage = errorMessage,
            onContinue   = {
                if (playerName.isBlank()) return@PseudoContent
                connecting   = true
                errorMessage = null
                onConnect { ok ->
                    connecting = false
                    if (ok) step = JoinStep.PICK
                    else errorMessage = "Impossible de rejoindre la session.\nVérifiez que vous êtes sur le même réseau WiFi."
                }
            },
            onBack = { step = JoinStep.SCAN },
        )

        // ── Choix du personnage ───────────────────────────────────────────────
        JoinStep.PICK -> PickCharacterContent(
            characters = characters,
            playerName = playerName,
            onPick     = onCharacterPicked,
            onCancel   = onCancel,
        )
    }
}