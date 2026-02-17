package cam.lucane.studio.log.rpg.ui.components.pdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.BackgroundDark
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@Composable
fun PdfOverlay(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }

    Surface(
        modifier = modifier.haze(hazeState).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = BackgroundDark.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.2f)),
        shadowElevation = 8.dp
    ) {
        // Effet glassmorphic en arrière-plan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Ligne navigation de page
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = canGoPrevious,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (canGoPrevious) TextPrimary else TextSecondary.copy(alpha = 0.3f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canGoPrevious) BorderSubtle else BorderSubtle.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Précédent",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Précédent", fontSize = 11.sp)
                    }

                    Text(
                        text = "Page $currentPage / $totalPages",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    OutlinedButton(
                        onClick = onNext,
                        enabled = canGoNext,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (canGoNext) TextPrimary else TextSecondary.copy(alpha = 0.3f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canGoNext) BorderSubtle else BorderSubtle.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Suivant", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            "Suivant",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}