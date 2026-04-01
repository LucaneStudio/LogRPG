package cam.lucane.studio.log.rpg.ui.combat.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.CombatConditionsRow
import cam.lucane.studio.log.rpg.ui.combat.components.ParticipantStatsSection
import cam.lucane.studio.log.rpg.ui.combat.components.SectionLabel
import cam.lucane.studio.log.rpg.ui.combat.components.common.AvatarBox
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.combat.components.common.StatusChip
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.combat.model.CombatState
import cam.lucane.studio.log.rpg.ui.combat.model.CombatStatus
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun CombatLeftPanel(
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
    modifier         : Modifier = Modifier,
) {
    val participant  = state.currentParticipant
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(ColorsSystem.BackgroundCard)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PanelHeader(participant, state.round, onEndCombat)
        HorizontalDivider(color = ColorsSystem.Divider)

        if (participant != null) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ParticipantStatsSection(
                    participant = participant,
                    onHpChange  = { delta -> onHpChange(participant.id, delta) },
                )
                HorizontalDivider(color = ColorsSystem.Divider)

                Row(modifier = Modifier.height(90.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    InitiativeSection(
                        modifier      = Modifier.weight(0.6f),
                        participant   = participant,
                        onSet         = { onInitiativeSet(participant.id, it) },
                        onBonusAdd    = { onBonusAdd(participant.id, it) },
                        onBonusRemove = { onBonusRemove(participant.id) },
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(Modifier.fillMaxHeight(0.9f).width(1.dp).background(ColorsSystem.Divider))
                    Spacer(Modifier.width(10.dp))
                    StatusSection(
                        modifier    = Modifier.weight(0.4f),
                        participant = participant,
                        onSetStatus = { onSetStatus(participant.id, it) },
                    )
                }

                HorizontalDivider(color = ColorsSystem.Divider)

                CombatConditionsRow(
                    conditions = participant.conditions,
                    onAdd      = { onAddCondition(participant.id, it) },
                    onRemove   = { onRemoveCondition(participant.id, it) },
                )
            }
        } else {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Aucun participant actif", fontSize = 13.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
            }
        }

        Button(
            onClick = {
                focusManager.clearFocus()   // ← vide le focus avant de changer de tour
                onNextTurn()
            },
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
        ) {
            Text("⚔️  Tour suivant →", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.BackgroundCard, fontFamily = NunitoFontFamily)
        }
    }
}

// ── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun PanelHeader(participant: CombatParticipant?, round: Int, onEndCombat: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        if (participant != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                AvatarBox(letter = participant.avatarLetter, color = participant.avatarColor, size = 36.dp, shape = RoundedCornerShape(11.dp), fontSize = 15)
                Column {
                    Text(participant.name, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                    Text(participant.type.name, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextDisabled, letterSpacing = 1.sp, fontFamily = NunitoFontFamily)
                }
            }
        } else {
            Spacer(Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Round $round",
                fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Blue, fontFamily = NunitoFontFamily,
                modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(ColorsSystem.BlueLight).padding(horizontal = 12.dp, vertical = 4.dp),
            )
            CardOptionButton(modifier = Modifier.height(30.dp), onClick = onEndCombat, color = ColorsSystem.Orange) {
                Text(modifier = Modifier.padding(horizontal = 10.dp), text = "🏁  Fin de combat", fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = NunitoFontFamily)
            }
        }
    }
}

// ── Initiative + bonus ────────────────────────────────────────────────────────

@Composable
private fun InitiativeSection(
    modifier     : Modifier,
    participant  : CombatParticipant,
    onSet        : (Int) -> Unit,
    onBonusAdd   : (Int) -> Unit,
    onBonusRemove: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    // ── Les deux états sont keyés sur participant.id ──────────────────────────
    // → reset automatique dès que le tour passe à un autre participant,
    //   peu importe si la valeur d'initiative est identique.
    var field by remember(participant.id) {
        val s = participant.initiative.toString()
        mutableStateOf(TextFieldValue(s, TextRange(0, s.length)))
    }
    var bonus by remember(participant.id) {
        mutableStateOf(
            if (participant.initiativeBonus != 0) participant.initiativeBonus.toString() else "0"
        )
    }

    // ── Vide le focus quand on change de participant (tour suivant) ───────────
    LaunchedEffect(participant.id) {
        focusManager.clearFocus()
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        SectionLabel("🎲  INITIATIVE")

        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {

            // Champ initiative (TextFieldValue pour sélection auto)
            OutlinedTextField(
                value           = field,
                onValueChange   = { field = it },
                modifier        = Modifier.width(80.dp),
                singleLine      = true,
                shape           = RoundedCornerShape(12.dp),
                label           = { Text("Initiative", color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily, fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { field.text.toIntOrNull()?.let(onSet); focusManager.clearFocus() }),
                colors          = combatTextFieldColors(),
                textStyle       = combatTextFieldStyle(),
            )

            // Champ bonus/malus
            CombatTextField(
                value           = bonus,
                onValueChange   = { bonus = it },
                label           = "Bonus / Malus",
                modifier        = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { bonus.toIntOrNull()?.let(onBonusAdd); focusManager.clearFocus() }),
            )

            // Valeur effective + badge bonus
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${participant.effectiveInitiative}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                    if (participant.hasBonus) Text("base ${participant.initiative}", fontSize = 9.sp, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)
                }
                if (participant.hasBonus) {
                    val positive = participant.initiativeBonus > 0
                    Text(
                        "${if (positive) "+" else ""}${participant.initiativeBonus}  ×",
                        fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                        color    = if (positive) ColorsSystem.GreenDark else ColorsSystem.RedDark, fontFamily = NunitoFontFamily,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (positive) ColorsSystem.GreenLight else ColorsSystem.RedLight)
                            .clickable(onClick = onBonusRemove)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                if (participant.hasPendingBonus) {
                    val positive = participant.pendingBonus > 0
                    Text(
                        "⏳ ${if (positive) "+" else ""}${participant.pendingBonus}",
                        color    = ColorsSystem.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ColorsSystem.BackgroundSurface).padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

// ── Statut KO / Fuite ────────────────────────────────────────────────────────

@Composable
private fun StatusSection(participant: CombatParticipant, onSetStatus: (CombatStatus) -> Unit, modifier: Modifier) {
    Column(modifier = modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        SectionLabel("⚡  STATUT")
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(
                label       = "💀  KO",
                active      = participant.status == CombatStatus.KO,
                activeColor = ColorsSystem.Red,
                activeBg    = ColorsSystem.RedLight,
                onClick     = { onSetStatus(if (participant.status == CombatStatus.KO) CombatStatus.ACTIVE else CombatStatus.KO) },
            )
            StatusChip(
                label       = "🏃  Fuite",
                active      = participant.status == CombatStatus.FLED,
                activeColor = ColorsSystem.Orange,
                activeBg    = ColorsSystem.OrangeLight,
                onClick     = { onSetStatus(if (participant.status == CombatStatus.FLED) CombatStatus.ACTIVE else CombatStatus.FLED) },
            )
        }
    }
}

// ── Helpers styles (champ TextFieldValue ne peut pas utiliser CombatTextField) ──

@Composable
private fun combatTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = ColorsSystem.TextSecondary,
    unfocusedBorderColor    = ColorsSystem.Divider,
    focusedContainerColor   = ColorsSystem.BackgroundCard,
    unfocusedContainerColor = ColorsSystem.BackgroundCard,
    cursorColor             = ColorsSystem.TextSecondary,
)

private fun combatTextFieldStyle() = androidx.compose.ui.text.TextStyle(
    fontFamily = NunitoFontFamily,
    fontSize   = 14.sp,
    fontWeight = FontWeight.Bold,
    color      = ColorsSystem.TextPrimary,
)