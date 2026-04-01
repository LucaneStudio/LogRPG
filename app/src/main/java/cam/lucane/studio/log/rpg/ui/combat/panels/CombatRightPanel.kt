package cam.lucane.studio.log.rpg.ui.combat.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.AddParticipantForm
import cam.lucane.studio.log.rpg.ui.combat.components.BonusDialog
import cam.lucane.studio.log.rpg.ui.combat.components.InitiativeCard
import cam.lucane.studio.log.rpg.ui.combat.model.*
import cam.lucane.studio.log.rpg.ui.components.common.DropdownAction
import cam.lucane.studio.log.rpg.ui.components.common.DropdownInputs
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun CombatRightPanel(
    state           : CombatState,
    onHpChange      : (id: String, delta: Int) -> Unit,
    onAddParticipant: (name: String, type: ParticipantType, maxHp: Int, initiative: Int) -> Unit,
    onSetStatus     : (id: String, status: CombatStatus) -> Unit,
    onRemove        : (id: String) -> Unit,
    onInitiativeSet : (id: String, value: Int) -> Unit,
    onBonusAdd      : (id: String, bonus: Int) -> Unit,
    onBonusRemove   : (id: String) -> Unit,
    modifier        : Modifier = Modifier,
) {
    var showAddForm        by remember { mutableStateOf(false) }
    var contextParticipant by remember { mutableStateOf<CombatParticipant?>(null) }
    var bonusTarget        by remember { mutableStateOf<CombatParticipant?>(null) }
    var expandedId         by remember { mutableStateOf<String?>(null) }
    val currentId = state.currentParticipant?.id

    Column(
        modifier = modifier.fillMaxHeight().background(ColorsSystem.BackgroundApp).padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("INITIATIVE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextDisabled, letterSpacing = 1.2.sp, fontFamily = NunitoFontFamily)
            Box(
                modifier         = Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(ColorsSystem.GreenLight),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = { showAddForm = !showAddForm }, modifier = Modifier.size(26.dp)) {
                    Text(if (showAddForm) "×" else "+", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.GreenDark, fontFamily = NunitoFontFamily)
                }
            }
        }

        if (showAddForm) {
            AddParticipantForm(onAdd = { name, type, maxHp, initiative ->
                onAddParticipant(name, type, maxHp, initiative)
                showAddForm = false
            })
            HorizontalDivider(color = ColorsSystem.Divider)
        }

        // ── Liste participants ────────────────────────────────────────────────
        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)) {
            items(state.sortedActive, key = { it.id }) { persona ->
                ParticipantCardWithMenu(
                    persona         = persona,
                    isCurrentTurn   = persona.id == currentId,
                    isExpanded      = persona.id == expandedId,
                    isMenuOpen      = contextParticipant?.id == persona.id,
                    onToggleExpand  = {
                        val canExpand = persona.id != currentId && persona.type != ParticipantType.PJ
                        if (canExpand) expandedId = if (expandedId == persona.id) null else persona.id
                    },
                    onOpenMenu      = { contextParticipant = persona },
                    onDismissMenu   = { contextParticipant = null },
                    onHpChange      = { delta -> onHpChange(persona.id, delta) },
                    onInitiativeSet = { onInitiativeSet(persona.id, it) },
                    onBonusRemove   = { onBonusRemove(persona.id) },
                    onSetStatus     = { onSetStatus(persona.id, it) },
                    onAddBonus      = { bonusTarget = persona; contextParticipant = null },
                    onRemove        = { onRemove(persona.id); contextParticipant = null },
                )
            }

            if (state.sortedInactive.isNotEmpty()) {
                item {
                    Text(
                        "─── KO / FUITE ───",
                        fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextDisabled,
                        letterSpacing = 1.sp, textAlign = TextAlign.Center, fontFamily = NunitoFontFamily,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    )
                }
                items(state.sortedInactive, key = { "${it.id}_ko" }) { persona ->
                    ParticipantCardWithMenu(
                        persona         = persona,
                        isCurrentTurn   = false,
                        isExpanded      = false,
                        isMenuOpen      = contextParticipant?.id == persona.id,
                        onToggleExpand  = {},
                        onOpenMenu      = { contextParticipant = persona },
                        onDismissMenu   = { contextParticipant = null },
                        onHpChange      = { delta -> onHpChange(persona.id, delta) },
                        onInitiativeSet = { onInitiativeSet(persona.id, it) },
                        onBonusRemove   = { onBonusRemove(persona.id) },
                        onSetStatus     = { onSetStatus(persona.id, it) },
                        onAddBonus      = { bonusTarget = persona; contextParticipant = null },
                        onRemove        = { onRemove(persona.id); contextParticipant = null },
                    )
                }
            }
        }
    }

    // Dialog bonus (inchangé)
    bonusTarget?.let { p ->
        BonusDialog(
            personaName = p.name,
            onConfirm   = { bonus -> onBonusAdd(p.id, bonus); bonusTarget = null },
            onDismiss   = { bonusTarget = null },
        )
    }
}

// ── Card + dropdown dans un même Box ─────────────────────────────────────────

@Composable
private fun ParticipantCardWithMenu(
    persona        : CombatParticipant,
    isCurrentTurn  : Boolean,
    isExpanded     : Boolean,
    isMenuOpen     : Boolean,
    onToggleExpand : () -> Unit,
    onOpenMenu     : () -> Unit,
    onDismissMenu  : () -> Unit,
    onHpChange     : (Int) -> Unit,
    onInitiativeSet: (Int) -> Unit,
    onBonusRemove  : () -> Unit,
    onSetStatus    : (CombatStatus) -> Unit,
    onAddBonus     : () -> Unit,
    onRemove       : () -> Unit,
) {
    Box {
        InitiativeCard(
            participant     = persona,
            isCurrentTurn   = isCurrentTurn,
            isExpanded      = isExpanded,
            onToggleExpand  = onToggleExpand,
            onLongClick     = { onOpenMenu() },
            onHpChange      = onHpChange,
            onInitiativeSet = onInitiativeSet,
            onBonusRemove   = onBonusRemove,
        )

        DropdownInputs(
            expanded  = isMenuOpen,
            onDismiss = onDismissMenu,
            offset    = IntOffset(x = 0, y = 90),
            actions   = buildContextActions(
                persona     = persona,
                onAddBonus  = onAddBonus,
                onSetStatus = onSetStatus,
                onRemove    = onRemove,
            ),
        )
    }
}

// ── Actions dynamiques selon le statut du participant ────────────────────────

private fun buildContextActions(
    persona    : CombatParticipant,
    onAddBonus : () -> Unit,
    onSetStatus: (CombatStatus) -> Unit,
    onRemove   : () -> Unit,
): List<DropdownAction> = buildList {
    add(DropdownAction(emoji = "🎲", label = "Bonus / Malus d'initiative", onClick = onAddBonus))

    if (persona.status != CombatStatus.KO)
        add(DropdownAction(emoji = "💀", label = "Marquer KO")       { onSetStatus(CombatStatus.KO) })
    if (persona.status != CombatStatus.FLED)
        add(DropdownAction(emoji = "🏃", label = "Marquer en fuite") { onSetStatus(CombatStatus.FLED) })
    if (persona.status != CombatStatus.ACTIVE)
        add(DropdownAction(emoji = "↩️", label = "Remettre actif")   { onSetStatus(CombatStatus.ACTIVE) })

    if (persona.linkedSocketId == null)
        add(DropdownAction(emoji = "🗑", label = "Retirer du combat", isDanger = true, onClick = onRemove))
}