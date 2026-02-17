package cam.lucane.studio.log.rpg.ui.components.common

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
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) color.copy(alpha = 0.15f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) color.copy(alpha = 0.4f) else BorderSubtle
        ),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) color else TextSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}