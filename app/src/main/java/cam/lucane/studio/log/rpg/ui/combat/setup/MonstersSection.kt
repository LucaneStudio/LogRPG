package cam.lucane.studio.log.rpg.ui.combat.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.combat.model.ParticipantType
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun MonstersSection(
    participants    : List<CombatParticipant>,
    onAdd           : (name: String, type: ParticipantType, maxHp: Int, initiative: Int) -> Unit,
    onSetInitiative : (id: String, value: Int) -> Unit,
    onRemove        : (id: String) -> Unit,
) {
    var name     by remember { mutableStateOf("") }
    var type     by remember { mutableStateOf(ParticipantType.MONSTER) }
    var hpText   by remember { mutableStateOf("") }
    var initText by remember { mutableStateOf("") }
    val isValid  = name.isNotBlank() && hpText.toIntOrNull() != null && initText.toIntOrNull() != null

    TypeChipsRow(selected = type, onSelect = { type = it })

    SetupTextField(value = name, onValueChange = { name = it }, label = "Nom", keyboardType = KeyboardType.Text, modifier = Modifier.fillMaxWidth())

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SetupTextField(value = hpText, onValueChange = { hpText = it }, label = "PV max", modifier = Modifier.weight(1f))
        SetupTextField(value = initText, onValueChange = { initText = it }, label = "Initiative", modifier = Modifier.weight(1f))
    }

    AddButton(enabled = isValid) {
        onAdd(name.trim(), type, hpText.toInt(), initText.toInt())
        name = ""; hpText = ""; initText = ""
    }

    val temporaries = participants.filter { it.linkedSocketId == null }
    if (temporaries.isNotEmpty()) {
        HorizontalDivider(color = ColorsSystem.Divider)
        temporaries.forEach { p ->
            TemporaryRow(persona = p, onSetInitiative = onSetInitiative, onRemove = { onRemove(p.id) })
        }
    }
}

@Composable
private fun TypeChipsRow(selected: ParticipantType, onSelect: (ParticipantType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            ParticipantType.MONSTER to ("👹  Monstre" to ColorsSystem.Red     to ColorsSystem.RedLight),
            ParticipantType.PNJ     to ("🗣  PNJ"     to ColorsSystem.Blue    to ColorsSystem.BlueLight),
        ).forEach { (t, colors) ->
            val (labelColor, bg) = colors
            val (label, color)   = labelColor
            val isSelected = t == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) bg else ColorsSystem.BackgroundSurface)
                    .clickable { onSelect(t) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) color else ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)
            }
        }
    }
}

@Composable
private fun AddButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9.dp))
            .background(if (enabled) ColorsSystem.GreenLight else ColorsSystem.BackgroundSurface)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("＋  Ajouter", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
            color = if (enabled) ColorsSystem.GreenDark else ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)
    }
}

@Composable
private fun TemporaryRow(
    persona        : CombatParticipant,
    onSetInitiative: (id: String, value: Int) -> Unit,
    onRemove       : () -> Unit,
) {
    var initText by remember(persona.id) { mutableStateOf(if (persona.initiative != 0) persona.initiative.toString() else "") }
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(26.dp).clip(CircleShape).background(persona.avatarColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(persona.avatarLetter, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.BackgroundCard, fontFamily = NunitoFontFamily)
        }
        Text(persona.name, modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
        SetupTextField(
            value         = initText,
            onValueChange = { initText = it; it.toIntOrNull()?.let { v -> onSetInitiative(persona.id, v) } },
            label         = "Init.",
            modifier      = Modifier.width(80.dp),
        )
        Text("×", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Red, fontFamily = NunitoFontFamily,
            modifier = Modifier.clickable(onClick = onRemove))
    }
}