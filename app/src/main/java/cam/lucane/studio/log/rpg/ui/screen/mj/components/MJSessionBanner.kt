package cam.lucane.studio.log.rpg.ui.screen.mj.components

import androidx.compose.foundation.background
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
fun MJSessionBanner(
    isRunning: Boolean,
    config: SessionConfig?,
    playerCount: Int,
    showQr: Boolean,
    onShowQR: () -> Unit
) {
    val color = if (isRunning) ColorsSystem.Green else AccentPurple

    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .background(color, RoundedCornerShape(99.dp))
                    )
                    Text(
                        text  = if (isRunning) "SESSION ACTIVE" else "MODE MAÎTRE DE JEU",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = color,
                        letterSpacing = 0.5.sp,
                        fontFamily = NunitoFontFamily
                    )
                }
                Text(
                    text = if (isRunning)
                        "${config?.token ?: ""} · $playerCount joueur${if (playerCount > 1) "s" else ""}"
                    else
                        "Aucune session en cours",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorsSystem.TextSecondary,
                    fontFamily = NunitoFontFamily,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (isRunning) {
                TextButton(
                    onClick = onShowQR,
                    shape   = RoundedCornerShape(8.dp),
                    colors  = ButtonDefaults.textButtonColors(contentColor = AccentPurple)
                ) {
                    Text(
                        text = "\uD83D\uDD17   " + if (showQr) "Fermer le QR Code" else "Ouvrir le QR Code",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily
                    )
                }
            }
        }
    }
}