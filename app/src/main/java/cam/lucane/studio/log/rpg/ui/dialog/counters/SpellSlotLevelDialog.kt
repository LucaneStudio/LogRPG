package cam.lucane.studio.log.rpg.ui.dialog.counters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetButtonRow
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun SpellSlotLevelDialog(
    slot: SpellSlot,
    mainBrush: Brush,
    onDismiss: () -> Unit,
    onConfirm: (newMax: Int) -> Unit
) {
    var value by remember { mutableStateOf(slot.max.toString()) }
    val parsed = value.toIntOrNull()

    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "📖 Niveau ${slot.level}",
        subtitle = "Nombre max de slots (0 = désactivé)"
    ) {
        // Grand champ central
        OutlinedTextField(
            value = value,
            onValueChange = { v ->
                if (v.length <= 1 && (v.isEmpty() || v.toIntOrNull() != null))
                    value = v
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                color = ColorsSystem.Blue,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorsSystem.Blue,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = ColorsSystem.BlueLight,
                unfocusedContainerColor = ColorsSystem.BlueLight,
                cursorColor = ColorsSystem.Blue
            )
        )

        Spacer(Modifier.height(12.dp))

        // Boutons rapides 0→9
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (0..4).forEach { v ->
                QuickSlotButton(value = v, selected = value == v.toString()) { value = v.toString() }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (5..9).forEach { v ->
                QuickSlotButton(value = v, selected = value == v.toString()) { value = v.toString() }
            }
        }

        Spacer(Modifier.height(20.dp))

        SheetButtonRow(
            onDismiss = onDismiss,
            onConfirm = { onConfirm(parsed?.coerceIn(0, 9) ?: 0) },
            confirmEnabled = parsed != null,
            confirmLabel = "Valider",
            confirmBrush = mainBrush
        )
    }
}

@Composable
private fun RowScope.QuickSlotButton(value: Int, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) ColorsSystem.Blue else ColorsSystem.BlueLight)
            .border(
                1.5.dp,
                if (selected) ColorsSystem.Blue else ColorsSystem.BlueLight,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = NunitoFontFamily,
            color = if (selected) Color.White else ColorsSystem.Blue
        )
    }
}
