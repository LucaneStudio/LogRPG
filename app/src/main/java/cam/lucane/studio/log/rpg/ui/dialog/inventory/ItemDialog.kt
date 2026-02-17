package cam.lucane.studio.log.rpg.ui.dialog.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Item
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary

@Composable
fun ItemDialog(
    title: String,
    initialItem: Item? = null,
    onDismiss: () -> Unit,
    onConfirm: (Item) -> Unit
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var quantity by remember { mutableStateOf((initialItem?.quantity ?: 1).toString()) }
    var weight by remember { mutableStateOf(initialItem?.weight ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "") }
    var notes by remember { mutableStateOf(initialItem?.notes ?: "") }
    var isConsumable by remember { mutableStateOf(initialItem?.isConsumable ?: false) }
    var isEquipped by remember { mutableStateOf(initialItem?.isEquipped ?: false) }

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
                    value = description, onValueChange = { description = it },
                    label = { Text("Description *") },
                    minLines = 2, maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Quantité") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight, onValueChange = { weight = it },
                        label = { Text("Poids") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = category, onValueChange = { category = it },
                    label = { Text("Catégorie") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(color = BorderSubtle)
                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Consommable", fontSize = 14.sp, color = TextPrimary)
                    Switch(
                        checked = isConsumable,
                        onCheckedChange = { isConsumable = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentRed, checkedTrackColor = AccentRed.copy(alpha = 0.3f))
                    )
                }
                if (!isConsumable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Équipé", fontSize = 14.sp, color = TextPrimary)
                        Switch(
                            checked = isEquipped,
                            onCheckedChange = { isEquipped = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = AccentGreen.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        (initialItem ?: Item(characterId = 0, name = "", description = "")).copy(
                            name = name,
                            description = description,
                            quantity = quantity.toIntOrNull() ?: 1,
                            weight = weight.ifBlank { null },
                            category = category.ifBlank { null },
                            notes = notes.ifBlank { null },
                            isConsumable = isConsumable,
                            isEquipped = if (isConsumable) false else isEquipped
                        )
                    )
                },
                enabled = name.isNotBlank() && description.isNotBlank()
            ) {
                Text(if (initialItem == null) "Ajouter" else "Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}