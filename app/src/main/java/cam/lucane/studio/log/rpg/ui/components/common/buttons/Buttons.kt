package cam.lucane.studio.log.rpg.ui.components.common.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary

@Composable
fun ControlButton(
    onClick: () -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun SmallIconBtn(
    onClick: () -> Unit,
    enabled: Boolean = true,
    tint: Color = TextSecondary,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(28.dp)
    ) {
        content()
    }
}
