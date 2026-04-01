package cam.lucane.studio.log.rpg.ui.combat.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.AvatarBox
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * Ligne d'initiative pour un participant dans l'écran de setup.
 * Extraite de CombatSetupSheet pour être réutilisable (setup screen / sheet).
 */
@Composable
fun SetupInitiativeRow(
    participant: CombatParticipant,
    onSet      : (Int) -> Unit,
    onRemove   : (() -> Unit)? = null,
) {
    var text by remember(participant.id) {
        mutableStateOf(participant.initiative.takeIf { it != 0 }?.toString() ?: "")
    }

    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AvatarBox(letter = participant.avatarLetter, color = participant.avatarColor)

        Text(
            text       = participant.name,
            modifier   = Modifier.weight(1f),
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = ColorsSystem.TextPrimary,
            fontFamily = NunitoFontFamily,
        )

        CombatTextField(
            value         = text,
            onValueChange = { text = it; it.toIntOrNull()?.let(onSet) },
            label         = "Init.",
            modifier      = Modifier.width(80.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        if (onRemove != null && participant.linkedSocketId == null) {
            TextButton(onClick = onRemove) {
                Text("✕", color = ColorsSystem.RedDark, fontFamily = NunitoFontFamily)
            }
        }
    }
}