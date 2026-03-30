package cam.lucane.studio.log.rpg.ui.screen.player

import androidx.compose.runtime.*
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen.PickCharacterContent
import cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen.PseudoContent

enum class JoinStep { SCAN, PSEUDO, PICK }

@Composable
fun JoinSessionScreen(
    characters: List<Character>,
    playerName: String,
    onNameChange: (String) -> Unit,
    onQRScanned: (String) -> Boolean,
    onConnect: (onResult: (Boolean) -> Unit) -> Unit,
    onCharacterPicked: (Character) -> Unit,
    onCancel: () -> Unit,
    // ✨ Permet de démarrer directement à PICK quand on change de perso
    startStep: JoinStep = JoinStep.SCAN
) {
    var step by remember { mutableStateOf(startStep) }
    var connecting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    when (step) {
        JoinStep.SCAN -> QRScannerScreen(
            onQRScanned = { content ->
                val ok = onQRScanned(content)
                if (ok) step = JoinStep.PSEUDO
                else errorMessage = "QR Code invalide"
            },
            onCancel = onCancel
        )

        JoinStep.PSEUDO -> PseudoContent(
            playerName   = playerName,
            onNameChange = onNameChange,
            connecting   = connecting,
            errorMessage = errorMessage,
            onContinue   = {
                if (playerName.isBlank()) return@PseudoContent
                connecting = true
                errorMessage = null
                onConnect { ok ->
                    connecting = false
                    if (ok) step = JoinStep.PICK
                    else errorMessage = "Impossible de rejoindre la session.\nVérifiez que vous êtes sur le même réseau WiFi."
                }
            },
            onBack = { step = JoinStep.SCAN }
        )

        JoinStep.PICK -> PickCharacterContent(
            characters = characters,
            playerName = playerName,
            onPick     = onCharacterPicked,
            onCancel   = onCancel
        )
    }
}