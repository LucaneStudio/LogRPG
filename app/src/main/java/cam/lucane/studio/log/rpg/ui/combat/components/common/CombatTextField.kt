package cam.lucane.studio.log.rpg.ui.combat.components.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * OutlinedTextField avec le style de l'application (couleurs, typo, coins arrondis).
 * Remplace les cinq duplications identiques dans le module combat.
 */
@Composable
fun CombatTextField(
    value          : String,
    onValueChange  : (String) -> Unit,
    label          : String,
    modifier       : Modifier        = Modifier,
    placeholder    : String          = "",
    singleLine     : Boolean         = true,
    isError        : Boolean         = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label, fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary, fontSize = 11.sp) },
        placeholder     = if (placeholder.isNotEmpty()) ({ Text(placeholder, fontFamily = NunitoFontFamily) }) else null,
        singleLine      = singleLine,
        isError         = isError,
        shape           = RoundedCornerShape(12.dp),
        modifier        = modifier,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = ColorsSystem.TextSecondary,
            unfocusedBorderColor    = ColorsSystem.Divider,
            focusedContainerColor   = ColorsSystem.BackgroundCard,
            unfocusedContainerColor = ColorsSystem.BackgroundCard,
            cursorColor             = ColorsSystem.TextSecondary,
        ),
        textStyle = TextStyle(
            fontFamily = NunitoFontFamily,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = ColorsSystem.TextPrimary,
        ),
    )
}