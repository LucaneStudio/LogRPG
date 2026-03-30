package cam.lucane.studio.log.rpg.ui.screen.mj.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun MJWaitingForPlayers() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                color       = ColorsSystem.Green,
                modifier    = Modifier.size(28.dp),
                strokeWidth = 2.dp
            )
            Text(
                text       = "En attente de connexions…",
                fontSize   = 12.sp,
                color      = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily
            )
        }
    }
}