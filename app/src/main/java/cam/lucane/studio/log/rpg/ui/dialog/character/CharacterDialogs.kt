package cam.lucane.studio.log.rpg.ui.dialog.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetButtonRow
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetLabel
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow

private val avatarGradients = listOf(
    ColorsSystem.GradientAvatarPurple,
    ColorsSystem.GradientAvatarGreen,
    ColorsSystem.GradientAvatarOrange,
    ColorsSystem.GradientAvatarPink,
    ColorsSystem.GradientAvatarYellow,
    ColorsSystem.GradientBarMana,
)

@Composable
fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCreated: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedGradientIdx by remember { mutableStateOf(0) }

    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "✨ Nouveau personnage"
    ) {
        // Champ nom
        SheetLabel(text = "NOM DU PERSONNAGE")
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .coloredShadow(ColorsSystem.Shadow.copy(.05f), 14.dp, 12.dp, offsetY = 3.dp),
            placeholder = {
                Text(
                    "Ex : Aelindra la Sage…",
                    fontFamily = NunitoFontFamily,
                    fontSize = 14.sp,
                    color = ColorsSystem.TextDisabled
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (name.isNotBlank()) onCreated(name.trim())
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorsSystem.Green,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = ColorsSystem.BackgroundCard,
                unfocusedContainerColor = ColorsSystem.BackgroundCard,
                cursorColor = ColorsSystem.Green
            ),
            textStyle = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsSystem.TextPrimary
            )
        )

        Spacer(Modifier.height(18.dp))

        // Chips couleur avatar
        SheetLabel(text = "COULEUR D'AVATAR")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            avatarGradients.forEachIndexed { index, brush ->
                val isSelected = selectedGradientIdx == index
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(brush)
                        .then(
                            if (isSelected) Modifier.border(
                                2.5.dp,
                                ColorsSystem.TextPrimary,
                                RoundedCornerShape(11.dp)
                            ) else Modifier
                        )
                        .clickable { selectedGradientIdx = index }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        SheetButtonRow(
            onDismiss = onDismiss,
            onConfirm = { if (name.isNotBlank()) onCreated(name.trim()) },
            confirmEnabled = name.isNotBlank(),
            confirmLabel = "Créer",
            confirmBrush = ColorsSystem.GradientGreen
        )
    }
}

@Composable
fun DeleteCharacterDialog(
    character: Character,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "Supprimer ${character.name} ?",
        subtitle = "Cette action est irréversible. Toutes les données seront perdues."
    ) {
        // Icône centrale
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ColorsSystem.RedLight),
                contentAlignment = Alignment.Center
            ) {
                Text("🗑", fontSize = 28.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        SheetButtonRow(
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            confirmLabel = "Supprimer",
            isDanger = true
        )
    }
}
