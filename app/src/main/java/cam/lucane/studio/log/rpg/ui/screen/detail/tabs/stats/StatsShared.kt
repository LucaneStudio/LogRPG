package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

/** Anneau de progression pour les widgets PERCENT. Mutualisé lecture/édition. */
@Composable
fun PercentRing(
    percent: Int,
    accent: Color,
    modifier: Modifier = Modifier,
    size: Int = 60,
) {
    val track = accent.copy(alpha = .2f)
    val sweep = (percent.coerceIn(0, 100) / 100f) * 360f

    Box(modifier = modifier.size(size.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 5.5.dp.toPx(), cap = StrokeCap.Round)
            drawArc(color = track,  startAngle = -90f, sweepAngle = 360f, useCenter = false, style = stroke)
            if (sweep > 0f)
                drawArc(color = accent, startAngle = -90f, sweepAngle = sweep, useCenter = false, style = stroke)
        }
        Text("$percent%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = accent)
    }
}

/** Poignée de drag 2×3 points. Visuel uniquement. */
@Composable
fun DragHandle(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.5.dp)) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp)) {
                repeat(2) {
                    Canvas(Modifier.size(2.5.dp)) { drawCircle(ColorsSystem.TextDisabled) }
                }
            }
        }
    }
}
