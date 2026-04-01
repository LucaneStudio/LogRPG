package cam.lucane.studio.log.rpg.ui.combat.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.session.PlayerSlot
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun PlayersSection(
    connectedPlayers: List<PlayerSlot>,
    selectedSockets : Set<String>,
    participants    : List<CombatParticipant>,
    onToggle        : (socketId: String, selected: Boolean) -> Unit,
    onSetInitiative : (id: String, value: Int) -> Unit,
) {
    connectedPlayers.forEach { slot ->
        PlayerRow(
            slot            = slot,
            checked         = slot.socketId in selectedSockets,
            linkedParticipant = participants.find { it.linkedSocketId == slot.socketId },
            onToggle        = { on -> onToggle(slot.socketId, on) },
            onSetInitiative = onSetInitiative,
        )
    }
}

@Composable
private fun PlayerRow(
    slot              : PlayerSlot,
    checked           : Boolean,
    linkedParticipant : CombatParticipant?,
    onToggle          : (Boolean) -> Unit,
    onSetInitiative   : (id: String, value: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (checked) ColorsSystem.GreenLight else ColorsSystem.BackgroundSurface)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(slot.info?.characterName ?: "—", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                Text(slot.info?.playerName ?: slot.playerName, fontSize = 10.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
            }
            Switch(
                checked         = checked,
                onCheckedChange = onToggle,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor   = ColorsSystem.BackgroundCard,
                    checkedTrackColor   = ColorsSystem.Green,
                    uncheckedThumbColor = ColorsSystem.BackgroundCard,
                    uncheckedTrackColor = ColorsSystem.TextDisabled,
                ),
            )
        }
        if (checked && linkedParticipant != null) {
            SetupTextField(
                value         = if (linkedParticipant.initiative != 0) linkedParticipant.initiative.toString() else "",
                onValueChange = { it.toIntOrNull()?.let { v -> onSetInitiative(linkedParticipant.id, v) } },
                label         = "Initiative",
                modifier      = Modifier.fillMaxWidth(),
            )
        }
    }
}