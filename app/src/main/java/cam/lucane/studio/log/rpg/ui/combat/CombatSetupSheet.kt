package cam.lucane.studio.log.rpg.ui.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.session.PlayerSlot
import cam.lucane.studio.log.rpg.ui.combat.components.AddParticipantForm
import cam.lucane.studio.log.rpg.ui.combat.components.SectionLabel
import cam.lucane.studio.log.rpg.ui.combat.model.CombatState
import cam.lucane.studio.log.rpg.ui.combat.model.ParticipantType
import cam.lucane.studio.log.rpg.ui.combat.setup.SetupInitiativeRow
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

private enum class SetupStep { PLAYERS, MONSTERS }

/**
 * Setup en deux étapes (sheet) :
 *  - PLAYERS  : sélection des PJs connectés
 *  - MONSTERS : création des monstres / PNJ + initiatives
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatSetupSheet(
    state               : CombatState,
    connectedPlayers    : List<PlayerSlot>,
    onAddSessionPlayers : (List<PlayerSlot>) -> Unit,
    onAddParticipant    : (name: String, type: ParticipantType, maxHp: Int, initiative: Int) -> Unit,
    onSetInitiative     : (id: String, value: Int) -> Unit,
    onRemove            : (id: String) -> Unit,
    onLaunch            : () -> Unit,
    onCancel            : () -> Unit,
    onDismiss           : () -> Unit,
) {
    val hasPlayers      = connectedPlayers.isNotEmpty()
    var step            by remember { mutableStateOf(if (hasPlayers) SetupStep.PLAYERS else SetupStep.MONSTERS) }
    var selectedSockets by remember { mutableStateOf(connectedPlayers.map { it.socketId }.toSet()) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (hasPlayers) StepIndicator(current = step)

            when (step) {

                // ── Étape 1 : sélection des PJs ──────────────────────────────
                SetupStep.PLAYERS -> {
                    Text("🧙  Participants", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                    Text("Sélectionne les joueurs qui participent au combat.", fontSize = 12.sp, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)

                    connectedPlayers.forEach { slot ->
                        val checked = slot.socketId in selectedSockets
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (checked) ColorsSystem.GreenLight else ColorsSystem.BackgroundSurface)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(slot.info?.characterName ?: "—", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                                Text(slot.info?.playerName ?: slot.playerName, fontSize = 10.sp, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)
                            }
                            Checkbox(
                                checked         = checked,
                                onCheckedChange = { on ->
                                    selectedSockets = if (on) selectedSockets + slot.socketId else selectedSockets - slot.socketId
                                },
                                colors = CheckboxDefaults.colors(checkedColor = ColorsSystem.Green),
                            )
                        }
                    }

                    Button(
                        onClick  = { onAddSessionPlayers(connectedPlayers.filter { it.socketId in selectedSockets }); step = SetupStep.MONSTERS },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
                    ) {
                        Text("Suivant  →", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.BackgroundCard, fontFamily = NunitoFontFamily)
                    }
                    TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                        Text("Annuler", fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
                    }
                }

                // ── Étape 2 : monstres / PNJ + initiatives ───────────────────
                SetupStep.MONSTERS -> {
                    Text("👹  Monstres & PNJ", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                    Text("Ajoute les ennemis et PNJ, puis saisis les initiatives.", fontSize = 12.sp, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)

                    AddParticipantForm(onAdd = onAddParticipant)

                    if (state.participants.isNotEmpty()) {
                        HorizontalDivider(color = ColorsSystem.Divider)
                        SectionLabel("🎲  INITIATIVES")
                        state.participants.forEach { participant ->
                            SetupInitiativeRow(
                                participant = participant,
                                onSet       = { onSetInitiative(participant.id, it) },
                                onRemove    = { onRemove(participant.id) },
                            )
                        }
                        Button(
                            onClick  = onLaunch,
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
                        ) {
                            Text("Lancer le combat  ⚔️", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.BackgroundCard, fontFamily = NunitoFontFamily)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (hasPlayers) {
                            TextButton(onClick = { step = SetupStep.PLAYERS }, modifier = Modifier.weight(1f)) {
                                Text("←  Retour", fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
                            }
                        }
                        TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                            Text("Annuler", fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(current: SetupStep) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        listOf(SetupStep.PLAYERS to "Joueurs", SetupStep.MONSTERS to "Combat").forEachIndexed { index, (step, label) ->
            val active = current == step
            Text(
                text       = "${index + 1}. $label",
                fontSize   = 11.sp,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Normal,
                color      = if (active) ColorsSystem.Green else ColorsSystem.TextDisabled,
                fontFamily = NunitoFontFamily,
                modifier   = Modifier.clip(RoundedCornerShape(99.dp)).background(if (active) ColorsSystem.GreenLight else ColorsSystem.BackgroundSurface).padding(horizontal = 12.dp, vertical = 4.dp),
            )
            if (index == 0) Text("  →  ", fontSize = 11.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
        }
    }
}