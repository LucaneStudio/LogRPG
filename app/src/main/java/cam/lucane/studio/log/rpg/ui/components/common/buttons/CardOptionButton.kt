package cam.lucane.studio.log.rpg.ui.components.common.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

@Composable
fun CardOptionButton(
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    content: @Composable () -> Unit
){
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        enabled = enabled,
        border = BorderStroke(width = 1.dp, ColorsSystem.Divider),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ColorsSystem.BackgroundSurface,
            disabledContainerColor = ColorsSystem.BackgroundSurface,
            contentColor = color,
            disabledContentColor = color
        ),
    ) {
        content.invoke()
    }
}