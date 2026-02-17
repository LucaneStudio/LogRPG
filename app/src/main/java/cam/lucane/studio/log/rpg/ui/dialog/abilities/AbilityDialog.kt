package cam.lucane.studio.log.rpg.ui.dialog.abilities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AbilityDialog(
    title: String,
    initialName: String = "",
    initialDesc: String = "",
    initialCost: String = "",
    initialRange: String = "",
    initialDuration: String = "",
    initialCategory: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc) }
    var cost by remember { mutableStateOf(initialCost) }
    var range by remember { mutableStateOf(initialRange) }
    var duration by remember { mutableStateOf(initialDuration) }
    var category by remember { mutableStateOf(initialCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nom *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc, onValueChange = { desc = it },
                    label = { Text("Description *") },
                    minLines = 2, maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cost, onValueChange = { cost = it },
                        label = { Text("Coût") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = range, onValueChange = { range = it },
                        label = { Text("Portée") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = duration, onValueChange = { duration = it },
                        label = { Text("Durée") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category, onValueChange = { category = it },
                        label = { Text("Catégorie") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, desc, cost, range, duration, category) },
                enabled = name.isNotBlank() && desc.isNotBlank()
            ) {
                Text(if (initialName.isEmpty()) "Ajouter" else "Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}