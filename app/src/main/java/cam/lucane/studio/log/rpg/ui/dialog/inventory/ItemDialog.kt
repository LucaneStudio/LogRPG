package cam.lucane.studio.log.rpg.ui.dialog.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Item
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetButtonRow
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetDivider
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetLabel
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun ItemDialog(
    title: String,
    mainColor: Color,
    mainBrush: Brush,
    initialItem: Item? = null,
    onDismiss: () -> Unit,
    onConfirm: (Item) -> Unit
) {
    var name        by remember { mutableStateOf(initialItem?.name ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var quantity    by remember { mutableStateOf((initialItem?.quantity ?: 1).toString()) }
    var weight      by remember { mutableStateOf(initialItem?.weight ?: "") }
    var category    by remember { mutableStateOf(initialItem?.category ?: "") }
    var notes       by remember { mutableStateOf(initialItem?.notes ?: "") }
    var isConsumable by remember { mutableStateOf(initialItem?.isConsumable ?: false) }
    var isEquipped  by remember { mutableStateOf(initialItem?.isEquipped ?: false) }

    @Composable
    fun Field(label: String, value: String, onChange: (String) -> Unit, placeholder: String = "", minLines: Int = 1, keyboard: KeyboardType = KeyboardType.Text, modifier: Modifier = Modifier.fillMaxWidth()) {

        Column(modifier) {
            SheetLabel(text = label)
            Spacer(Modifier.height(2.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        placeholder,
                        fontFamily = NunitoFontFamily,
                        fontSize = 13.sp,
                        color = ColorsSystem.TextDisabled
                    )
                },
                singleLine = minLines == 1,
                minLines = minLines,
                maxLines = if (minLines > 1) 4 else 1,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboard),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = mainColor,
                    unfocusedBorderColor = ColorsSystem.Divider,
                    focusedContainerColor = ColorsSystem.BackgroundCard,
                    unfocusedContainerColor = ColorsSystem.BackgroundCard,
                    cursorColor = mainColor
                ),
                textStyle = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorsSystem.TextPrimary
                )
            )
            Spacer(Modifier.height(10.dp))
        }
    }

    BaseBottomSheet(onDismiss = onDismiss, title = title) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            Field("NOM *", name, { name = it }, "Épée longue, Potion…")
            Field("DESCRIPTION *", description, { description = it }, "Effets, origine, histoire…", minLines = 2)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field("QUANTITÉ", quantity, { quantity = it }, "1", keyboard = KeyboardType.Number, modifier = Modifier.weight(1f))
                Field("POIDS", weight, { weight = it }, "1 kg", modifier = Modifier.weight(1f))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field("CATÉGORIE", category, { category = it }, "Arme, Armure…", modifier = Modifier.weight(1f))
                Field("NOTES", notes, { notes = it }, "Optionnel", modifier = Modifier.weight(1f))
            }

            SheetDivider()
            Spacer(Modifier.height(12.dp))

            // Toggle Consommable
            ToggleRow(
                label = "🧪 Consommable",
                desc = "L'objet est utilisable et a une quantité",
                checked = isConsumable,
                activeColor = ColorsSystem.Red,
                onToggle = {
                    isConsumable = it
                    if (it) isEquipped = false
                }
            )

            Spacer(Modifier.height(6.dp))

            // Toggle Équipé (masqué si consommable)
            if (!isConsumable) {
                ToggleRow(
                    label = "⚔️ Équipé",
                    desc = "L'objet est actuellement porté",
                    checked = isEquipped,
                    activeColor = ColorsSystem.Green,
                    onToggle = { isEquipped = it }
                )
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(14.dp))

            SheetButtonRow(
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirm(
                        (initialItem ?: Item(characterId = 0, name = "", description = "")).copy(
                            name = name.trim(),
                            description = description.trim(),
                            quantity = quantity.toIntOrNull() ?: 1,
                            weight = weight.ifBlank { null },
                            category = category.ifBlank { null },
                            notes = notes.ifBlank { null },
                            isConsumable = isConsumable,
                            isEquipped = if (isConsumable) false else isEquipped
                        )
                    )
                },
                confirmEnabled = name.isNotBlank() && description.isNotBlank(),
                confirmLabel = if (initialItem == null) "Ajouter" else "Enregistrer",
                confirmBrush = mainBrush,
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    desc: String,
    checked: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (checked) activeColor.copy(.07f) else ColorsSystem.BackgroundSurface)
            .border(
                1.5.dp,
                if (checked) activeColor.copy(.25f) else ColorsSystem.Divider,
                RoundedCornerShape(14.dp)
            )
            .clickable { onToggle(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
            Text(desc, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
        }
        // Pill toggle
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(if (checked) activeColor else ColorsSystem.Divider),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(20.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White)
            )
        }
    }
}
