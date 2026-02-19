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

// Dialog de modification d'un seul niveau — ouvert via appui long sur la case

@Composable
fun SpellSlotLevelDialog(
    slot: SpellSlot,
    onDismiss: () -> Unit,
    onConfirm: (newMax: Int) -> Unit
) {
    var value by remember { mutableStateOf(slot.max.toString()) }
    val parsed = value.toIntOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Niveau ${slot.level} — Emplacements", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nombre max de slots disponibles (0 = désactivé)", fontSize = 12.sp, color = TextSecondary)
                OutlinedTextField(
                    value = value,
                    onValueChange = { v ->
                        if (v.length <= 1 && (v.isEmpty() || v.toIntOrNull() != null))
                            value = v
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
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
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(parsed?.coerceIn(0, 9) ?: 0) },
                enabled = parsed != null
            ) {
                Text("Valider", color = AccentPurple, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TextSecondary) }
        }
    )
}