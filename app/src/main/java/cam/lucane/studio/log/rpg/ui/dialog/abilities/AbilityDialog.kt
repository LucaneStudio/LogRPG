package cam.lucane.studio.log.rpg.ui.dialog.abilities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetButtonRow
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetLabel
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

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
    mainColor: Color,
    mainBrush: Brush,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var name     by remember { mutableStateOf(initialName) }
    var desc     by remember { mutableStateOf(initialDesc) }
    var cost     by remember { mutableStateOf(initialCost) }
    var range    by remember { mutableStateOf(initialRange) }
    var duration by remember { mutableStateOf(initialDuration) }
    var category by remember { mutableStateOf(initialCategory) }

    @Composable
    fun Field(
        label: String,
        value: String,
        onChange: (String) -> Unit,
        placeholder: String = "",
        minLines: Int = 1,
        modifier: Modifier = Modifier.fillMaxWidth()
    ) {
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

            Field("NOM *", name, { name = it }, "Boule de feu, Téléportation…")
            Field("DESCRIPTION *", desc, { desc = it }, "Effets, conditions, règles…", minLines = 3)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field("COÛT", cost, { cost = it }, "3 mana", modifier = Modifier.weight(1f))
                Field("PORTÉE", range, { range = it }, "36 m", modifier = Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field("DURÉE", duration, { duration = it }, "1 action", modifier = Modifier.weight(1f))
                Field("CATÉGORIE", category, { category = it }, "Évocation", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(6.dp))

            SheetButtonRow(
                onDismiss = onDismiss,
                onConfirm = { onConfirm(name.trim(), desc.trim(), cost.trim(), range.trim(), duration.trim(), category.trim()) },
                confirmEnabled = name.isNotBlank() && desc.isNotBlank(),
                confirmLabel = if (initialName.isEmpty()) "Ajouter" else "Enregistrer",
                confirmBrush = mainBrush
            )
        }
    }
}
