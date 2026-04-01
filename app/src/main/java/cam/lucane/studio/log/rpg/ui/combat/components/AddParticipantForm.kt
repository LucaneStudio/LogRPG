package cam.lucane.studio.log.rpg.ui.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.combat.model.ParticipantType
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/** Formulaire d'ajout d'un participant temporaire (Monstre ou PNJ). */
@Composable
fun AddParticipantForm(
    onAdd: (name: String, type: ParticipantType, maxHp: Int, initiative: Int) -> Unit,
) {
    var name           by remember { mutableStateOf("") }
    var type           by remember { mutableStateOf(ParticipantType.MONSTER) }
    var maxHpText      by remember { mutableStateOf("") }
    var initiativeText by remember { mutableStateOf("") }
    val isValid = name.isNotBlank() && maxHpText.toIntOrNull() != null && initiativeText.toIntOrNull() != null

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(ParticipantType.MONSTER, ParticipantType.PNJ).forEach { t ->
                TypeChip(type = t, selected = t == type, onClick = { type = t }, modifier = Modifier.weight(1f))
            }
        }

        CombatTextField(
            value         = name,
            onValueChange = { name = it },
            label         = "Nom",
            placeholder   = if (type == ParticipantType.MONSTER) "Gobelin archer…" else "Marchand…",
            modifier      = Modifier.fillMaxWidth(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CombatTextField(
                value           = maxHpText,
                onValueChange   = { maxHpText = it },
                label           = "PV max",
                modifier        = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            CombatTextField(
                value           = initiativeText,
                onValueChange   = { initiativeText = it },
                label           = "Initiative",
                modifier        = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isValid) ColorsSystem.Green else ColorsSystem.BackgroundSurface)
                .clickable(enabled = isValid) {
                    onAdd(name.trim(), type, maxHpText.toInt(), initiativeText.toInt())
                    name = ""; maxHpText = ""; initiativeText = ""
                }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "＋  Ajouter au combat",
                fontSize   = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = if (isValid) ColorsSystem.BackgroundCard else ColorsSystem.TextDisabled,
                fontFamily = NunitoFontFamily,
            )
        }
    }
}

@Composable
private fun TypeChip(type: ParticipantType, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (label, color) = when (type) {
        ParticipantType.MONSTER -> "👹  Monstre" to ColorsSystem.Red
        ParticipantType.PNJ     -> "🗣  PNJ"     to ColorsSystem.Blue
        else                    -> "?"           to ColorsSystem.TextDisabled
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) color else ColorsSystem.BackgroundSurface)
            .border(1.5.dp, if (selected) color else ColorsSystem.Divider, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = if (selected) ColorsSystem.BackgroundCard else ColorsSystem.TextSecondary,
            fontFamily = NunitoFontFamily,
        )
    }
}