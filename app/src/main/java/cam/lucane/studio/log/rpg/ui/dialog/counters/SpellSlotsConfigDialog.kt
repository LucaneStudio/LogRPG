package cam.lucane.studio.log.rpg.ui.dialog.counters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetButtonRow
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetLabel
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun SpellSlotsConfigDialog(
    slots: List<SpellSlot>,
    mainBrush: Brush,
    onDismiss: () -> Unit,
    onConfirm: (List<SpellSlot>) -> Unit
) {
    val maxValues = remember { slots.map { it.max.toString() }.toMutableStateList() }

    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "📖 Emplacements de sorts",
        subtitle = "Nombre max par niveau · 0 = désactivé"
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Grille 3 colonnes
            slots.chunked(3).forEachIndexed { rowIndex, rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSlots.forEachIndexed { colIndex, slot ->
                        val i = rowIndex * 3 + colIndex
                        val currentVal = maxValues[i].toIntOrNull() ?: 0
                        val isActive = currentVal > 0

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Label niveau
                            Text(
                                text = "Niv. ${slot.level}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = NunitoFontFamily,
                                color = if (isActive) ColorsSystem.Blue else ColorsSystem.TextDisabled,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp
                            )

                            // Valeur centrale cliquable (−/valeur/+)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isActive) ColorsSystem.BlueLight
                                        else ColorsSystem.BackgroundSurface
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isActive) ColorsSystem.Blue.copy(.3f) else ColorsSystem.Divider,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = maxValues[i].ifEmpty { "0" },
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = NunitoFontFamily,
                                    color = if (isActive) ColorsSystem.Blue else ColorsSystem.TextDisabled
                                )
                            }

                            // Boutons − et +
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // −
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ColorsSystem.BlueLight)
                                        .border(1.dp, ColorsSystem.Blue.copy(.2f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            val current = maxValues[i].toIntOrNull() ?: 0
                                            maxValues[i] = (current - 1).coerceAtLeast(0).toString()
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("−", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NunitoFontFamily, color = ColorsSystem.Blue)
                                }
                                // +
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ColorsSystem.BlueLight)
                                        .border(1.dp, ColorsSystem.Blue.copy(.2f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            val current = maxValues[i].toIntOrNull() ?: 0
                                            maxValues[i] = (current + 1).coerceAtMost(9).toString()
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NunitoFontFamily, color = ColorsSystem.Blue)
                                }
                            }
                        }
                    }

                    // Remplissage si la dernière ligne est incomplète
                    repeat(3 - rowSlots.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Bouton reset tout à 0
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorsSystem.RedLight)
                    .border(1.5.dp, ColorsSystem.Red.copy(.25f), RoundedCornerShape(12.dp))
                    .clickable { maxValues.indices.forEach { maxValues[it] = "0" } }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🗑 Tout remettre à 0",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily,
                    color = ColorsSystem.Red
                )
            }

            Spacer(Modifier.height(4.dp))

            SheetButtonRow(
                onDismiss = onDismiss,
                onConfirm = {
                    val updated = slots.mapIndexed { i, slot ->
                        val newMax = maxValues[i].toIntOrNull()?.coerceIn(0, 9) ?: 0
                        slot.copy(max = newMax, current = slot.current.coerceAtMost(newMax))
                    }
                    onConfirm(updated)
                },
                confirmLabel = "Valider",
                confirmBrush = mainBrush
            )
        }
    }
}
