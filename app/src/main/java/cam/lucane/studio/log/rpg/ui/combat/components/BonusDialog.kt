package cam.lucane.studio.log.rpg.ui.combat.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/** Dialog de saisie d'un bonus d'initiative temporaire (positif ou négatif). */
@Composable
fun BonusDialog(personaName: String, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    val value = text.toIntOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color.White,
        title = { Text("Bonus d'initiative", fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(personaName, fontSize = 12.sp, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                CombatTextField(
                    value           = text,
                    onValueChange   = { text = it },
                    label           = "Valeur (ex: +3 ou -2)",
                    isError         = text.isNotBlank() && value == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier        = Modifier.fillMaxWidth(),
                )
                Text(
                    "Un bonus positif augmente l'initiative, négatif la réduit. Tap sur le badge pour le retirer.",
                    fontSize = 10.sp, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { value?.let { onConfirm(it) } }, enabled = value != null) {
                Text("Appliquer", fontFamily = NunitoFontFamily, color = if (value != null) ColorsSystem.Green else ColorsSystem.TextDisabled)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", fontFamily = NunitoFontFamily, color = ColorsSystem.Red) }
        },
    )
}