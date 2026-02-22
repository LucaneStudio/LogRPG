package cam.lucane.studio.log.rpg.ui.components.common.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow

@Composable
fun FloatingDotButton(
    onClick: () -> Unit,
    dashColor: Color,
    labelColor: Color
) {

    Box(
        modifier = Modifier.size(55.dp)
            .drawBehind {
                val strokeWidth = 4.dp.toPx()

                // 1. Fond blanc plein (comble les intervalles)
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(18.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )

                // 2. Pointillés par-dessus
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(18.dp.toPx()),
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(10.dp.toPx(), 6.dp.toPx()),
                            phase = 0f
                        )
                    )
                )
            }
            .coloredShadow(
                color = labelColor.copy(0.3f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            )
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ColorsSystem.BackgroundCard,
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "+",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = NunitoFontFamily,
                color = labelColor
            )
        }
    }
}