package cam.lucane.studio.log.rpg.ui.combat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.AvatarBox
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.combat.model.CombatStatus
import cam.lucane.studio.log.rpg.ui.combat.model.ParticipantType
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * Carte d'un participant dans la liste d'initiative.
 * - Tap simple  → déplie/replie les boutons HP (+1 / -1)
 * - Long press  → menu statut (inchangé)
 * - Tap init    → champ éditable inline
 * - Badge bonus → tap pour supprimer
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InitiativeCard(
    participant    : CombatParticipant,
    isCurrentTurn  : Boolean,
    isExpanded     : Boolean,
    onToggleExpand : () -> Unit,
    onLongClick    : (CombatParticipant) -> Unit,
    onHpChange     : (delta: Int) -> Unit,
    onInitiativeSet: (Int) -> Unit,
    onBonusRemove  : () -> Unit,
    modifier       : Modifier = Modifier,
) {
    val isInactive  = participant.status != CombatStatus.ACTIVE
    val bgColor     = when {
        isCurrentTurn -> ColorsSystem.GreenLight
        isExpanded    -> ColorsSystem.BackgroundSurface
        else          -> ColorsSystem.BackgroundCard
    }
    val borderColor = when {
        isCurrentTurn -> ColorsSystem.Green
        isExpanded    -> ColorsSystem.TextDisabled
        else          -> ColorsSystem.Divider
    }
    var editingInit by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isInactive) 0.4f else 1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor)
            .combinedClickable(
                onClick    = { if (!editingInit) onToggleExpand() },
                onLongClick = { onLongClick(participant) },
            ),
    ) {
        // ── Ligne principale ─────────────────────────────────────────────────
        Row(
            modifier              = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Initiative (tap pour éditer)
            if (editingInit) {
                InlineInitiativeField(
                    current  = participant.initiative,
                    onSubmit = { onInitiativeSet(it); editingInit = false },
                    onCancel = { editingInit = false },
                    modifier = Modifier.width(44.dp),
                )
            } else {
                Column(
                    modifier            = Modifier
                        .width(36.dp)
                        .clickable { editingInit = true },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text       = participant.effectiveInitiative.toString(),
                        fontSize   = 12.sp, fontWeight = FontWeight.ExtraBold,
                        color      = if (isCurrentTurn) ColorsSystem.GreenDark else ColorsSystem.TextSecondary,
                        fontFamily = NunitoFontFamily,
                    )
                    if (participant.hasBonus) {
                        BonusBadge(bonus = participant.initiativeBonus, onRemove = onBonusRemove)
                    }
                }
            }

            AvatarBox(letter = participant.avatarLetter, color = participant.avatarColor, size = 28.dp, shape = RoundedCornerShape(9.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(participant.name, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(participant.type.displayLabel, fontSize = 9.sp, color = participant.type.labelColor, fontFamily = NunitoFontFamily)
            }

            if (!isInactive) {
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("${participant.currentHp}/${participant.maxHp}", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Red, fontFamily = NunitoFontFamily)
                    Box(modifier = Modifier.width(32.dp).height(4.dp).clip(CircleShape).background(ColorsSystem.RedLight)) {
                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(participant.hpPercent).clip(CircleShape).background(ColorsSystem.Red))
                    }
                }
            } else {
                StatusBadge(participant.status)
            }
        }

        // ── Section HP dépliée ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded && !isInactive,
            enter   = expandVertically(),
            exit    = shrinkVertically(),
        ) {
            Column {
                HorizontalDivider(color = ColorsSystem.Divider)
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text("PV", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
                    listOf(-1, +1).forEach { delta ->
                        Button(
                            onClick        = { onHpChange(delta) },
                            modifier       = Modifier.weight(1f).height(28.dp),
                            shape          = RoundedCornerShape(7.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors         = ButtonDefaults.buttonColors(
                                containerColor = if (delta < 0) ColorsSystem.RedLight else ColorsSystem.GreenLight,
                            ),
                        ) {
                            Text(
                                text       = if (delta > 0) "+1" else "-1",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = if (delta < 0) ColorsSystem.RedDark else ColorsSystem.GreenDark,
                                fontFamily = NunitoFontFamily,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InlineInitiativeField(current: Int, onSubmit: (Int) -> Unit, onCancel: () -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    val focusManager   = LocalFocusManager.current
    var textField by remember { mutableStateOf(TextFieldValue(current.toString(), TextRange(0, current.toString().length))) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    BasicTextField(
        value           = textField,
        onValueChange   = { textField = it },
        singleLine      = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            textField.text.toIntOrNull()?.let { onSubmit(it) } ?: onCancel()
            focusManager.clearFocus()
        }),
        modifier  = modifier
            .focusRequester(focusRequester)
            .clip(RoundedCornerShape(6.dp))
            .background(ColorsSystem.BackgroundSurface)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily, textAlign = TextAlign.Center),
    )
}

@Composable
private fun BonusBadge(bonus: Int, onRemove: () -> Unit) {
    val positive = bonus > 0
    Text(
        text       = "${if (positive) "+" else ""}$bonus  ×",
        fontSize   = 7.sp, fontWeight = FontWeight.ExtraBold,
        color      = if (positive) ColorsSystem.GreenDark else ColorsSystem.RedDark,
        fontFamily = NunitoFontFamily,
        modifier   = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (positive) ColorsSystem.GreenLight else ColorsSystem.RedLight)
            .clickable(onClick = onRemove)
            .padding(horizontal = 3.dp, vertical = 1.dp),
    )
}

@Composable
private fun StatusBadge(status: CombatStatus) {
    val (label, bg, fg) = when (status) {
        CombatStatus.KO   -> Triple("KO",    ColorsSystem.RedLight,         ColorsSystem.RedDark)
        CombatStatus.FLED -> Triple("Fuite", ColorsSystem.BackgroundSurface, ColorsSystem.TextSecondary)
        else              -> return
    }
    Text(label, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = fg, fontFamily = NunitoFontFamily,
        modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(bg).padding(horizontal = 5.dp, vertical = 2.dp))
}

private val ParticipantType.displayLabel get() = when (this) {
    ParticipantType.PJ      -> "PJ"
    ParticipantType.PNJ     -> "PNJ"
    ParticipantType.MONSTER -> "Monstre"
}
private val ParticipantType.labelColor get() = when (this) {
    ParticipantType.PJ      -> ColorsSystem.Purple
    ParticipantType.PNJ     -> ColorsSystem.Blue
    ParticipantType.MONSTER -> ColorsSystem.Red
}