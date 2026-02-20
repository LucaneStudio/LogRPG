package cam.lucane.studio.log.rpg.ui.components.pdf

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.components.common.buttons.PrimaryButton
import cam.lucane.studio.log.rpg.ui.theme.BackgroundDark
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
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
    modifier: Modifier = Modifier,
    mainColor: Color
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrimaryButton (
                onClick = onPrevious,
                enabled = canGoPrevious,
                modifier = Modifier.height(38.dp).weight(1f),
                color = if(canGoPrevious) mainColor else ColorsSystem.TextDisabled,
                borderColor = if(canGoPrevious) mainColor.copy(0.35f) else ColorsSystem.PhoneBorder.copy(0.5f)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Précédent",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Précédent",
                    fontSize = 12.sp,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Text(
                text = "Page $currentPage / $totalPages",
                fontSize = 12.sp,
                color = ColorsSystem.TextSecondary,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NunitoFontFamily,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            PrimaryButton (
                onClick = onNext,
                enabled = canGoNext,
                modifier = Modifier.height(38.dp).weight(1f),
                color = if(canGoNext) mainColor else ColorsSystem.TextDisabled,
                borderColor = if(canGoNext) mainColor.copy(0.35f) else ColorsSystem.PhoneBorder.copy(0.5f)
            ) {
                Text(
                    "Suivant",
                    fontSize = 12.sp,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                )
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
