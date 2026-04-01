package cam.lucane.studio.log.rpg.ui.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

private val PRESET_CONDITIONS = listOf(
    "Empoisonné", "Étourdi", "À terre", "Aveuglé",
    "Effrayé", "Paralysé", "Charmé", "Invisible",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CombatConditionsRow(conditions: List<String>, onAdd: (String) -> Unit, onRemove: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        SectionLabel("⚡  CONDITIONS")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement   = Arrangement.spacedBy(4.dp),
        ) {
            conditions.forEach { ConditionBadge(it) { onRemove(it) } }
            AddConditionButton { showDialog = true }
        }
    }

    if (showDialog) {
        ConditionPickerDialog(
            current   = conditions,
            onConfirm = { onAdd(it); showDialog = false },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun ConditionBadge(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(ColorsSystem.OrangeLight)
            .padding(horizontal = 9.dp, vertical = 3.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label, fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Orange, fontFamily = NunitoFontFamily)
        Text("×", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Orange, fontFamily = NunitoFontFamily,
            modifier = Modifier.clickable { onRemove() })
    }
}

@Composable
private fun AddConditionButton(onClick: () -> Unit) {
    Text(
        "＋",
        fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily,
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(ColorsSystem.BackgroundSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConditionPickerDialog(current: List<String>, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var custom by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color.White,
        title = { Text("Ajouter une condition", fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PRESET_CONDITIONS.filter { it !in current }.forEach { preset ->
                        Text(
                            text      = preset,
                            fontSize  = 12.sp, fontWeight = FontWeight.Bold,
                            color     = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily,
                            modifier  = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorsSystem.BackgroundSurface)
                                .clickable { onConfirm(preset) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
                HorizontalDivider(color = ColorsSystem.Divider)
                CombatTextField(
                    value           = custom,
                    onValueChange   = { custom = it },
                    label           = "Condition personnalisée",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { if (custom.isNotBlank()) onConfirm(custom.trim()) }),
                    modifier        = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (custom.isNotBlank()) onConfirm(custom.trim()) }, enabled = custom.isNotBlank()) {
                Text("Ajouter", fontFamily = NunitoFontFamily, color = if (custom.isNotBlank()) ColorsSystem.Green else ColorsSystem.TextDisabled)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", fontFamily = NunitoFontFamily, color = ColorsSystem.Red) }
        },
    )
}