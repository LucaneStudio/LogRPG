package cam.lucane.studio.log.rpg.ui.dialog.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Note
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetButtonRow
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetLabel
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun NoteDialog(
    note: Note? = null,    // null = création, non-null = renommage
    mainColor: Color,
    onConfirm: (title: String) -> Unit,
    onDismiss: () -> Unit,
    mainBrush: Brush,
) {
    val isEdit = note != null
    var title by remember { mutableStateOf(note?.title ?: "") }

    BaseBottomSheet(
        onDismiss = onDismiss,
        title = if (isEdit) "Renommer la note" else "📝 Nouvelle note"
    ) {
        SheetLabel(text = "TITRE DE LA NOTE")
        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Ex : Session du 12 mars, Lore du monde…",
                    fontFamily = NunitoFontFamily,
                    fontSize = 13.sp,
                    color = ColorsSystem.TextDisabled
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (title.isNotBlank()) onConfirm(title.trim())
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorsSystem.Purple,
                unfocusedBorderColor = ColorsSystem.Divider,
                focusedContainerColor = ColorsSystem.BackgroundCard,
                unfocusedContainerColor = ColorsSystem.BackgroundCard,
                cursorColor = mainColor
            ),
            textStyle = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsSystem.TextPrimary
            )
        )

        Spacer(Modifier.height(24.dp))

        SheetButtonRow(
            onDismiss = onDismiss,
            onConfirm = { if (title.isNotBlank()) onConfirm(title.trim()) },
            confirmEnabled = title.isNotBlank(),
            confirmLabel = if (isEdit) "Renommer" else "Créer",
            confirmBrush = mainBrush
        )
    }
}
