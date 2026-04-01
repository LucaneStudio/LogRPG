package cam.lucane.studio.log.rpg.ui.combat.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import cam.lucane.studio.log.rpg.ui.combat.components.*
import cam.lucane.studio.log.rpg.ui.combat.components.common.AvatarBox  // unused here but kept for consistency
import cam.lucane.studio.log.rpg.ui.combat.model.*
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * Layout portrait du mode combat.
 *
 * Structure :
 *  ┌─────────────────────────────────┐
 *  │  PersonaTimelineRow (scrollable)│
 *  ├─────────────────────────────────┤
 *  │  Détail du persona actif        │
 *  ├─────────────────────────────────┤
 *  │  Bouton Tour suivant            │
 *  └─────────────────────────────────┘
 */
@Composable
fun CombatPortraitLayout(
    state            : CombatState,
    onHpChange       : (id: String, delta: Int) -> Unit,
    onAddCondition   : (id: String, condition: String) -> Unit,
    onRemoveCondition: (id: String, condition: String) -> Unit,
    onNextTurn       : () -> Unit,
    onEndCombat      : () -> Unit,
    onInitiativeSet  : (id: String, value: Int) -> Unit,
    onBonusAdd       : (id: String, bonus: Int) -> Unit,
    onBonusRemove    : (id: String) -> Unit,
    onSetStatus      : (id: String, status: CombatStatus) -> Unit,
    onRemove         : (id: String) -> Unit,
    onAddParticipant : (name: String, type: ParticipantType, maxHp: Int, initiative: Int) -> Unit,
    modifier         : Modifier = Modifier,
) {
    var selectedId  by remember(state.currentParticipant?.id) { mutableStateOf(state.currentParticipant?.id) }
    val selectedPersona = state.participants.find { it.id == selectedId } ?: state.currentParticipant
    var bonusTarget by remember { mutableStateOf<CombatParticipant?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().background(ColorsSystem.BackgroundApp).systemBarsPadding(),
    ) {
        PortraitHeader(round = state.round, onEndCombat = onEndCombat, onAddPersona = { showAddSheet = true })

        PersonaTimelineRow(state = state, onSelectPersona = { selectedId = it.id })

        HorizontalDivider(color = ColorsSystem.Divider)

        if (selectedPersona != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(ColorsSystem.BackgroundCard)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (selectedPersona.id != state.currentParticipant?.id) {
                    Text("👁  Consultation — pas le tour de ce persona", fontSize = 10.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
                }
                ParticipantStatsSection(participant = selectedPersona, onHpChange = { delta -> onHpChange(selectedPersona.id, delta) })
                HorizontalDivider(color = ColorsSystem.Divider)
                CombatConditionsRow(
                    conditions = selectedPersona.conditions,
                    onAdd      = { onAddCondition(selectedPersona.id, it) },
                    onRemove   = { onRemoveCondition(selectedPersona.id, it) },
                )
                HorizontalDivider(color = ColorsSystem.Divider)
                InitiativePortraitRow(
                    persona         = selectedPersona,
                    onInitiativeSet = { onInitiativeSet(selectedPersona.id, it) },
                    onAddBonus      = { bonusTarget = selectedPersona },
                    onRemoveBonus   = { onBonusRemove(selectedPersona.id) },
                )
            }
        } else {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Aucun participant actif", fontSize = 13.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
            }
        }

        Button(
            onClick  = onNextTurn,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
        ) {
            Text("⚔️  Tour suivant →", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.BackgroundCard, fontFamily = NunitoFontFamily)
        }
    }

    bonusTarget?.let { p ->
        BonusDialog(
            personaName = p.name,
            onConfirm   = { bonus -> onBonusAdd(p.id, bonus); bonusTarget = null },
            onDismiss   = { bonusTarget = null },
        )
    }
}

@Composable
private fun PortraitHeader(round: Int, onEndCombat: () -> Unit, onAddPersona: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(ColorsSystem.BackgroundCard).padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            "Round $round",
            fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Yellow, fontFamily = NunitoFontFamily,
            modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(ColorsSystem.YellowLight).padding(horizontal = 12.dp, vertical = 4.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onAddPersona) {
                Text("＋ Persona", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.GreenDark, fontFamily = NunitoFontFamily)
            }
            Text(
                "🏁",
                fontSize = 14.sp,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ColorsSystem.BackgroundSurface).clickable(onClick = onEndCombat).padding(6.dp),
            )
        }
    }
}

@Composable
private fun InitiativePortraitRow(
    persona        : CombatParticipant,
    onInitiativeSet: (Int) -> Unit,
    onAddBonus     : () -> Unit,
    onRemoveBonus  : () -> Unit,
) {
    var editingInit by remember { mutableStateOf(false) }
    var initText    by remember(persona.id) { mutableStateOf(persona.initiative.toString()) }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        SectionLabel("🎲  INITIATIVE")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (persona.hasBonus) {
                Text(
                    "${if (persona.initiativeBonus > 0) "+" else ""}${persona.initiativeBonus}  ×",
                    fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                    color    = if (persona.initiativeBonus > 0) ColorsSystem.GreenDark else ColorsSystem.RedDark,
                    fontFamily = NunitoFontFamily,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (persona.initiativeBonus > 0) ColorsSystem.GreenLight else ColorsSystem.RedLight)
                        .clickable(onClick = onRemoveBonus)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
            TextButton(onClick = onAddBonus) {
                Text("+ Bonus", fontSize = 10.sp, color = ColorsSystem.Green, fontFamily = NunitoFontFamily)
            }
            if (editingInit) {
                OutlinedTextField(
                    value         = initText,
                    onValueChange = { initText = it },
                    singleLine    = true,
                    modifier      = Modifier.width(72.dp),
                    label         = { Text("Init.", fontFamily = NunitoFontFamily) },
                )
                TextButton(onClick = { initText.toIntOrNull()?.let { onInitiativeSet(it) }; editingInit = false }) {
                    Text("OK", fontFamily = NunitoFontFamily, color = ColorsSystem.Green)
                }
            } else {
                Text(
                    "${persona.effectiveInitiative}",
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily,
                    modifier = Modifier.clickable { editingInit = true },
                )
            }
        }
    }
}