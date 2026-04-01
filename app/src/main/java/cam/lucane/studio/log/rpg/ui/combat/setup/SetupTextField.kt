package cam.lucane.studio.log.rpg.ui.combat.setup

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * OutlinedTextField au style combat, partagé entre PlayersSection et MonstersSection.
 */
@Composable
fun SetupTextField(
    value        : String,
    onValueChange: (String) -> Unit,
    label        : String,
    modifier     : Modifier = Modifier,
    keyboardType : KeyboardType = KeyboardType.Number,
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = {
            Text(label, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily, fontSize = 11.sp)
        },
        singleLine      = true,
        shape           = RoundedCornerShape(10.dp),
        modifier        = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle       = TextStyle(
            fontFamily = NunitoFontFamily,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = ColorsSystem.TextPrimary,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = ColorsSystem.TextSecondary,
            unfocusedBorderColor    = ColorsSystem.Divider,
            focusedContainerColor   = ColorsSystem.BackgroundCard,
            unfocusedContainerColor = ColorsSystem.BackgroundSurface,
            cursorColor             = ColorsSystem.TextSecondary,
            focusedLabelColor       = ColorsSystem.TextSecondary,
            unfocusedLabelColor     = ColorsSystem.TextDisabled,
        ),
    )
}