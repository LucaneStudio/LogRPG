package cam.lucane.studio.log.rpg.ui.dialog.character

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CropOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val boxSize = size.minDimension * 0.84f
        val left = (size.width - boxSize) / 2f
        val top = (size.height - boxSize) / 2f
        val cornerRadius = boxSize * 0.12f

        val cropRect = Rect(Offset(left, top), Size(boxSize, boxSize))

        // Fond sombre avec trou carré arrondi
        drawPath(
            path = Path().apply {
                addRect(Rect(Offset.Zero, size))
                addRoundRect(RoundRect(cropRect, CornerRadius(cornerRadius)))
            },
            color = Color.Black.copy(alpha = 0.6f),
            style = Fill
        )

        // Bordure du carré arrondi
        drawRoundRect(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(left, top),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(cornerRadius),
            style = Stroke(width = 2.dp.toPx())
        )

        // Lignes des tiers
        val thirdsColor = Color.White.copy(alpha = 0.25f)
        val strokeWidth = 1.dp.toPx()
        val third = boxSize / 3f
        listOf(1f, 2f).forEach { i ->
            drawLine(thirdsColor, Offset(left + third * i, top), Offset(left + third * i, top + boxSize), strokeWidth)
            drawLine(thirdsColor, Offset(left, top + third * i), Offset(left + boxSize, top + third * i), strokeWidth)
        }
    }
}