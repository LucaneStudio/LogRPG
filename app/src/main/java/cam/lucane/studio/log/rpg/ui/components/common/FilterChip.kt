package cam.lucane.studio.log.rpg.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = NunitoFontFamily,
        color = if (selected) Color.White else ColorsSystem.TextDisabled,
        modifier = Modifier
            .coloredShadow(
                color = if (selected) color.copy(.3f) else ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 99.dp,
                blurRadius = if (selected) 12.dp else 8.dp,
                offsetY = 2.dp
            )
            .background(
                color = if (selected) color else ColorsSystem.BackgroundCard,
                shape = RoundedCornerShape(99.dp)
            )
            .clickable { onClick.invoke() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        letterSpacing = 0.3.sp
    )
}