package cam.lucane.studio.log.rpg.ui.dialog.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun SheetButtonRow(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
    confirmLabel: String = "Valider",
    confirmBrush: Brush = ColorsSystem.GradientGreen,
    isDanger: Boolean = false
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Annuler
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(99.dp))
                .background(ColorsSystem.BackgroundSurface)
                .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                .clickable { onDismiss() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Annuler",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color = ColorsSystem.TextSecondary
            )
        }

        // Confirmer
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(99.dp))
                .then(
                    if (isDanger)
                        Modifier
                            .background(if (confirmEnabled) ColorsSystem.RedLight else ColorsSystem.Divider)
                            .border(1.5.dp, if (confirmEnabled) ColorsSystem.Red.copy(.3f) else Color.Transparent, RoundedCornerShape(99.dp))
                    else
                        Modifier.background(if (confirmEnabled) confirmBrush else Brush.linearGradient(listOf(ColorsSystem.Divider, ColorsSystem.Divider)))
                )
                .clickable(enabled = confirmEnabled) { onConfirm() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = confirmLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color = if (!confirmEnabled) ColorsSystem.TextDisabled else if (isDanger) ColorsSystem.Red else Color.White
            )
        }
    }
}

@Composable
fun SheetDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.5.dp)
            .background(ColorsSystem.Divider)
    )
}

@Composable
fun SheetLabel(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        fontFamily = NunitoFontFamily,
        color = ColorsSystem.TextDisabled,
        letterSpacing = 1.2.sp
    )
}
