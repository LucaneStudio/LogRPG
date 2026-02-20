package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import cam.lucane.studio.log.rpg.ui.components.common.buttons.SmallIconBtn
import cam.lucane.studio.log.rpg.ui.dialog.counters.SpellSlotLevelDialog
import cam.lucane.studio.log.rpg.ui.dialog.counters.SpellSlotsConfigDialog
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun SpellSlotsCard(
    character: Character,
    viewModel: CharacterDetailViewModel,
    mainColor: Color,
    onEditMode: (() -> Unit)? = null
) {
    val slots = character.getSpellSlots()
    var showConfigDialog by remember { mutableStateOf(false) }
    var editingSlot by remember { mutableStateOf<SpellSlot?>(null) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "EMPLACEMENTS DE SORTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 1.5.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    SmallIconBtn(onClick = { showConfigDialog = true }) {
                        Icon(Icons.Default.Edit, "Configurer", modifier = Modifier.size(16.dp))
                    }
                    onEditMode?.let { action ->
                        SmallIconBtn(onClick = action) {
                            Icon(
                                Icons.Default.Settings,
                                "Changer de mode",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "👆 Tap = utiliser  ·  👆👆 Double tap = récupérer  ·  ⏱ Long = modifier",
                fontSize = 9.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
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
                            mainColor = mainColor,
                            modifier = Modifier.weight(1f),
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

            // Boutons de reset
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        // Repos court : reset niveaux 1 et 2 uniquement
                        val updated = slots.map {
                            if (it.level <= 2) it.copy(current = it.max) else it
                        }
                        viewModel.updateSpellSlots(updated)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.4f))
                ) {
                    Text("☀️ Repos court", color = AccentGold, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = {
                        // Repos long : reset tous les niveaux actifs
                        viewModel.updateSpellSlots(slots.map { it.copy(current = it.max) })
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ManaBlue.copy(alpha = 0.4f))
                ) {
                    Text("🌙 Repos long", color = ManaBlue, fontSize = 12.sp)
                }
            }
        }
    }

    // Dialog config globale (tous les niveaux)
    if (showConfigDialog) {
        SpellSlotsConfigDialog(
            slots = slots,
            onDismiss = { showConfigDialog = false },
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
            }
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
        !slot.isActive  -> TextSecondary.copy(alpha = 0.3f)
        slot.isDepleted -> TextSecondary.copy(alpha = 0.4f)
        else            -> mainColor
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (!slot.isActive) GlassSurface else mainColor.copy(0.1f)),
        border = BorderStroke(1.dp, BorderSubtle),
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
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "NIV. ${slot.level}",
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = if (!slot.isActive) "—" else "${slot.current}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = if (!slot.isActive) "" else "/ ${slot.max}",
                fontSize = 9.sp,
                color = TextSecondary
            )
        }
    }
}

// ── Helper ─────────────────────────────────────────────────────────────────

private fun List<SpellSlot>.updateSlot(updated: SpellSlot): List<SpellSlot> =
    map { if (it.level == updated.level) updated else it }