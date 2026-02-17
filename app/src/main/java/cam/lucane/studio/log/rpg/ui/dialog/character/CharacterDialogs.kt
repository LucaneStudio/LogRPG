package cam.lucane.studio.log.rpg.ui.dialog.character

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.theme.HealthRed

@Composable
fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCreated: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau personnage") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom du personnage") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreated(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun DeleteCharacterDialog(
    character: Character,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Supprimer ${character.name} ?") },
        text = { Text("Cette action est irréversible. Toutes les données seront perdues.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Supprimer", color = HealthRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}