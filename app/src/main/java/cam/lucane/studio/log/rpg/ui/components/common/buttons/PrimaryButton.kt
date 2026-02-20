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
fun PrimaryButton(
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    borderColor: Color,
    content: @Composable () -> Unit
){
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        enabled = enabled,
        border = BorderStroke(width = 2.dp, borderColor),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White,
            contentColor = color,
            disabledContentColor = color
        ),
    ) {
        content.invoke()
    }
}