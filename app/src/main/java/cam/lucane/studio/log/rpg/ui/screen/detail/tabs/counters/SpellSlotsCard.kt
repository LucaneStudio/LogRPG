package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import cam.lucane.studio.log.rpg.data.entity.getSpellSlots
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.PrimaryButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.SmallIconBtn
import cam.lucane.studio.log.rpg.ui.dialog.counters.SpellSlotLevelDialog
import cam.lucane.studio.log.rpg.ui.dialog.counters.SpellSlotsConfigDialog
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.utils.getAccentBrushByCharacterId
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun SpellSlotsCard(
    character: Character,
    viewModel: CharacterDetailViewModel,
    onEditMode: (() -> Unit)? = null
) {
    val slots = character.getSpellSlots()
    var showConfigDialog by remember { mutableStateOf(false) }
    var editingSlot by remember { mutableStateOf<SpellSlot?>(null) }
    val mainBrush = getAccentBrushByCharacterId(character.id)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColorsSystem.BackgroundCard),
        modifier = Modifier
            .fillMaxWidth()
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "\uD83D\uDCD6 EMPLACEMENTS DE SORTS",
                    fontSize = 11.sp,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Black,
                    color = ColorsSystem.TextDisabled,
                    letterSpacing = 1.5.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CardOptionButton(
                        modifier = Modifier.size(26.dp),
                        onClick = { showConfigDialog = true },
                        color = ColorsSystem.Blue
                    ) {
                        Text(
                            text = "✏️",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = NunitoFontFamily
                        )
                    }

                    onEditMode?.let { action ->
                        CardOptionButton(
                            modifier = Modifier.size(26.dp),
                            onClick = action,
                            color = ColorsSystem.Blue
                        ) {
                            Text(
                                text = "⚙️",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = NunitoFontFamily
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "👆 Tap = utiliser  ·  👆👆 Double tap = récupérer  ·  ⏱ Long = modifier",
                fontSize = 8.sp,
                color = ColorsSystem.TextDisabled,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = NunitoFontFamily
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Grille 3×3
            slots.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { slot ->
                        SpellSlotCell(
                            slot = slot,
                            mainColor = ColorsSystem.Blue,
                            modifier = Modifier
                                .height(70.dp)
                                .weight(1f),
                            onUse = {
                                if (slot.current > 0)
                                    viewModel.updateSpellSlots(slots.updateSlot(slot.copy(current = slot.current - 1)))
                            },
                            onRecover = {
                                if (slot.current < slot.max)
                                    viewModel.updateSpellSlots(slots.updateSlot(slot.copy(current = slot.current + 1)))
                            },
                            onEditLevel = { editingSlot = slot }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Boutons de reset
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton (
                    onClick = {
                        val updated = slots.map {
                            if (it.level <= 2) it.copy(current = it.max) else it
                        }
                        viewModel.updateSpellSlots(updated)
                    },
                    modifier = Modifier.height(36.dp).weight(1f),
                    color = ColorsSystem.Orange,
                    borderColor = ColorsSystem.Orange.copy(0.35f)
                ) {
                    Text(
                        text = "☀️ Repos court",
                        fontSize = 12.sp,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                PrimaryButton(
                    onClick = {
                        // Repos long : reset tous les niveaux actifs
                        viewModel.updateSpellSlots(slots.map { it.copy(current = it.max) })
                    },
                    modifier = Modifier.height(36.dp).weight(1f),
                    color = ColorsSystem.Blue,
                    borderColor = ColorsSystem.Blue.copy(0.35f)
                ) {
                    Text(
                        text = "🌙 Repos long",
                        fontSize = 12.sp,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }

    // Dialog config globale (tous les niveaux)
    if (showConfigDialog) {
        SpellSlotsConfigDialog(
            slots = slots,
            onDismiss = { showConfigDialog = false },
            mainBrush = mainBrush,
            onConfirm = { updated ->
                viewModel.updateSpellSlots(updated)
                showConfigDialog = false
            }
        )
    }

    // Dialog edit d'un niveau (appui long)
    editingSlot?.let { slot ->
        SpellSlotLevelDialog(
            slot = slot,
            onDismiss = { editingSlot = null },
            onConfirm = { newMax ->
                val updated = slots.updateSlot(
                    slot.copy(max = newMax, current = slot.current.coerceAtMost(newMax))
                )
                viewModel.updateSpellSlots(updated)
                editingSlot = null
            },
            mainBrush = mainBrush
        )
    }
}

// ── Cellule individuelle ───────────────────────────────────────────────────

@Composable
private fun SpellSlotCell(
    slot: SpellSlot,
    modifier: Modifier = Modifier,
    mainColor: Color,
    onUse: () -> Unit,
    onRecover: () -> Unit,
    onEditLevel: () -> Unit
) {
    val accentColor = when {
        !slot.isActive  -> ColorsSystem.TextDisabled
        slot.isDepleted -> ColorsSystem.TextDisabled
        else            -> mainColor
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!slot.isActive) ColorsSystem.BackgroundSurface else mainColor.copy(0.1f)
        ),
        border = BorderStroke(1.dp, if(!slot.isActive)ColorsSystem.Divider else mainColor.copy(0.2f)),
        modifier = modifier.pointerInput(slot) {
            var tapCount = 0
            var tapJob: kotlinx.coroutines.Job? = null
            detectTapGestures(
                onTap = {
                    // Double tap détecté manuellement
                    tapCount++
                    if (tapCount == 1) {
                        tapJob = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            kotlinx.coroutines.delay(250)
                            if (tapCount == 1) onUse()
                            tapCount = 0
                        }
                    } else if (tapCount >= 2) {
                        tapJob?.cancel()
                        tapCount = 0
                        onRecover()
                    }
                },
                onLongPress = { onEditLevel() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "NIV. ${slot.level}",
                fontSize = 8.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Black,
                color = ColorsSystem.TextDisabled,
                letterSpacing = 0.5.sp
            )
            if (!slot.isActive) {
                Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .offset(y = (-5).dp)
                            .height(3.dp)
                            .background(color = ColorsSystem.TextDisabled, shape = CircleShape)
                    )
                }
            }
            else {
                Text(
                    text = "${slot.current}",
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )

                Text(
                    text = "/ ${slot.max}",
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = ColorsSystem.TextDisabled
                )
            }
        }
    }
}

// ── Helper ─────────────────────────────────────────────────────────────────

private fun List<SpellSlot>.updateSlot(updated: SpellSlot): List<SpellSlot> =
    map { if (it.level == updated.level) updated else it }