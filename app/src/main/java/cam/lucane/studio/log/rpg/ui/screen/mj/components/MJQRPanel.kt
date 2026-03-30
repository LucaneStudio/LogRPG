package cam.lucane.studio.log.rpg.ui.screen.mj.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.session.SessionConfig
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun MJQRPanel(config: SessionConfig) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AccentPurple.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentPurple.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text       = "📲 Rejoindre en cours de session",
                fontSize   = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = AccentPurple,
                fontFamily = NunitoFontFamily
            )
            QRCodeImage(
                content = config.toQRContent(),
                size    = 180.dp
            )
            Text(
                text       = "Code : ${config.token} · Même réseau WiFi",
                fontSize   = 10.sp,
                color      = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily
            )
            Text(
                text       = "IP : ${config.ip}:${config.port}",
                fontSize   = 10.sp,
                color      = ColorsSystem.TextDisabled,
                fontFamily = NunitoFontFamily
            )
        }
    }
}