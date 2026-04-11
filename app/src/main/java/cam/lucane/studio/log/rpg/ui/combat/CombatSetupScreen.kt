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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.session.PlayerSlot
import cam.lucane.studio.log.rpg.ui.combat.model.CombatState
import cam.lucane.studio.log.rpg.ui.combat.model.ParticipantType
import cam.lucane.studio.log.rpg.ui.combat.setup.*
import cam.lucane.studio.log.rpg.ui.components.common.AccordionSection
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

private enum class SetupSection { PLAYERS, LOCAL_CHARS, MONSTERS }

@Composable
fun CombatSetupScreen(
    state               : CombatState,
    connectedPlayers    : List<PlayerSlot>,
    localCharacters     : List<Character>,
    onAddSessionPlayers : (List<PlayerSlot>) -> Unit,
    onAddLocalCharacter : (Character) -> Unit,
    onAddParticipant    : (name: String, type: ParticipantType, maxHp: Int, initiative: Int) -> Unit,
    onSetInitiative     : (id: String, value: Int) -> Unit,
    onRemove            : (id: String) -> Unit,
    onLaunch            : () -> Unit,
    onCancel            : () -> Unit,
) {
    val hasPlayers     = connectedPlayers.isNotEmpty()
    val hasLocalChars  = localCharacters.isNotEmpty()

    // Section ouverte par défaut : joueurs connectés > persos locaux > monstres
    val defaultSection = when {
        hasPlayers    -> SetupSection.PLAYERS
        hasLocalChars -> SetupSection.LOCAL_CHARS
        else          -> SetupSection.MONSTERS
    }
    var expanded        by remember { mutableStateOf(defaultSection) }
    var selectedSockets by remember { mutableStateOf(emptySet<String>()) }

    val canLaunch = state.participants.isNotEmpty() && state.participants.all { it.initiative != 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
            .systemBarsPadding(),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .background(ColorsSystem.BackgroundCard)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                Text("Mode combat", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                Text("Configure les participants", fontSize = 11.sp, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)
            }
            TextButton(onClick = onCancel) {
                Text("Annuler", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorsSystem.Red, fontFamily = NunitoFontFamily)
            }
        }

        HorizontalDivider(color = ColorsSystem.Divider)

        SetupPersonaRow(participants = state.participants, onRemove = onRemove)

        HorizontalDivider(color = ColorsSystem.Divider)

        // ── Sections ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            // ── Joueurs connectés ─────────────────────────────────────────────
            if (hasPlayers) {
                AccordionSection(
                    title    = "🧙  Joueurs connectés",
                    subtitle = "${selectedSockets.size} sélectionné(s)",
                    expanded = expanded == SetupSection.PLAYERS,
                    onToggle = { expanded = SetupSection.PLAYERS },
                ) {
                    PlayersSection(
                        connectedPlayers = connectedPlayers,
                        selectedSockets  = selectedSockets,
                        participants     = state.participants,
                        onToggle         = { socketId, on ->
                            selectedSockets = if (on) selectedSockets + socketId else selectedSockets - socketId
                            if (on) {
                                onAddSessionPlayers(connectedPlayers.filter { it.socketId in selectedSockets })
                            } else {
                                state.participants
                                    .find { it.linkedSocketId == socketId }
                                    ?.let { onRemove(it.id) }
                            }
                        },
                        onSetInitiative = onSetInitiative,
                    )
                }
            }

            // ── Personnages locaux (MJ) ───────────────────────────────────────
            AccordionSection(
                title    = "🧑‍🤝‍🧑  Personnages locaux",
                subtitle = "${state.participants.count { it.localCharId != null }} ajouté(s)",
                expanded = expanded == SetupSection.LOCAL_CHARS,
                onToggle = { expanded = SetupSection.LOCAL_CHARS },
            ) {
                LocalCharactersSection(
                    localCharacters = localCharacters,
                    participants    = state.participants,
                    onToggle        = { character, on ->
                        if (on) {
                            onAddLocalCharacter(character)
                        } else {
                            state.participants
                                .find { it.localCharId == character.id }
                                ?.let { onRemove(it.id) }
                        }
                    },
                    onSetInitiative = onSetInitiative,
                )
            }

            // ── Monstres & PNJ manuels ────────────────────────────────────────
            AccordionSection(
                title    = "👹  Monstres & PNJ",
                subtitle = "${state.participants.count { it.linkedSocketId == null && it.localCharId == null }} ajouté(s)",
                expanded = expanded == SetupSection.MONSTERS,
                onToggle = { expanded = SetupSection.MONSTERS },
            ) {
                MonstersSection(
                    participants    = state.participants,
                    onAdd           = onAddParticipant,
                    onSetInitiative = onSetInitiative,
                    onRemove        = onRemove,
                )
            }
        }

        // ── Bouton lancer ─────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp).padding(bottom = 4.dp)) {
            if (!canLaunch && state.participants.isNotEmpty()) {
                Text(
                    "Tous les participants doivent avoir une initiative.",
                    fontSize  = 10.sp,
                    color     = ColorsSystem.TextSecondary,
                    fontFamily = NunitoFontFamily,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            Button(
                onClick  = onLaunch,
                enabled  = canLaunch,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = ColorsSystem.Green,
                    disabledContainerColor = ColorsSystem.GreenLight,
                ),
            ) {
                Text(
                    "⚔️  Lancer le combat",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily,
                    color      = if (canLaunch) ColorsSystem.BackgroundCard else ColorsSystem.TextSecondary,
                )
            }
        }
    }
}