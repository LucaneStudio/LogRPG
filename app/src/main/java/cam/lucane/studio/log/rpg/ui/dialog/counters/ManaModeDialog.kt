package cam.lucane.studio.log.rpg.ui.dialog.counters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.data.entity.ManaMode
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun ManaModeDialog(
    currentMode: ManaMode,
    onDismiss: () -> Unit,
    onConfirm: (ManaMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Mode du compteur", color = TextPrimary) },
        text = {
            Column {
                ManaMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(mode) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = mode == currentMode,
                            onClick = { onConfirm(mode) },
                            colors = RadioButtonDefaults.colors(selectedColor = AccentPurple)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = when (mode) {
                                    ManaMode.MANA        -> "💧 Mana"
                                    ManaMode.SPELL_SLOTS -> "📖 Emplacements de sorts"
                                },
                                color = TextPrimary
                            )
                            Text(
                                text = when (mode) {
                                    ManaMode.MANA        -> "Barre classique avec +/−"
                                    ManaMode.SPELL_SLOTS -> "Grille par niveau, tap / double tap"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer", color = TextSecondary) }
        }
    )
}