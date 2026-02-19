package cam.lucane.studio.log.rpg.ui.dialog.counters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import cam.lucane.studio.log.rpg.ui.theme.*

// Dialog de configuration globale — tous les niveaux d'un coup
// Accessible via le bouton ✏ dans le header de SpellSlotsCard

@Composable
fun SpellSlotsConfigDialog(
    slots: List<SpellSlot>,
    onDismiss: () -> Unit,
    onConfirm: (List<SpellSlot>) -> Unit
) {
    val maxValues = remember { slots.map { it.max.toString() }.toMutableStateList() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Emplacements de sorts", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Nombre max de slots par niveau (0 = désactivé)",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Grille 3 colonnes
                slots.chunked(3).forEachIndexed { rowIndex, rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEachIndexed { colIndex, slot ->
                            val i = rowIndex * 3 + colIndex
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Niv. ${slot.level}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = maxValues[i],
                                    onValueChange = { v ->
                                        if (v.length <= 1 && (v.isEmpty() || v.toIntOrNull() != null))
                                            maxValues[i] = v
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(
                                        textAlign = TextAlign.Center,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentPurple,
                                        unfocusedBorderColor = BorderSubtle,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val updated = slots.mapIndexed { i, slot ->
                    val newMax = maxValues[i].toIntOrNull()?.coerceIn(0, 9) ?: 0
                    slot.copy(max = newMax, current = slot.current.coerceAtMost(newMax))
                }
                onConfirm(updated)
            }) {
                Text("Valider", color = AccentPurple, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TextSecondary) }
        }
    )
}