package cam.lucane.studio.log.rpg.ui.screen.list.components.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun HealthManaBar(
    label: String,
    current: Int,
    max: Int,
    color: Color
) {
    val progress = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f
    val hasOverflow = current > max
    val overflowProgress = if (hasOverflow && max > 0) ((current - max).toFloat() / max).coerceIn(0f, 1f) else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.width(20.dp),
            fontFamily = NunitoFontFamily
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ColorsSystem.Divider)
        ) {
            // Barre normale (100% si overflow)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (hasOverflow) 1f else progress)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
            )

            // Barre overflow par-dessus (bleu clair)
            if (hasOverflow) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(overflowProgress)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorsSystem.RedExtraBar)
                )
            }
        }
    }
}