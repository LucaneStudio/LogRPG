package cam.lucane.studio.log.rpg.ui.combat.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/** Chip cliquable affichant un statut actif/inactif (KO, Fuite, etc.). */
@Composable
fun StatusChip(
    label      : String,
    active     : Boolean,
    activeColor: Color,
    activeBg   : Color,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier,
) {
    Text(
        text       = label,
        fontSize   = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color      = if (active) activeColor else ColorsSystem.TextSecondary,
        fontFamily = NunitoFontFamily,
        modifier   = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) activeBg else ColorsSystem.BackgroundSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}