package cam.lucane.studio.log.rpg.ui.screen.mj.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.session.SessionConfig
import cam.lucane.studio.log.rpg.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MJSessionBanner(
    isRunning   : Boolean,
    config      : SessionConfig?,
    playerCount : Int,
    showQr      : Boolean,
    onShowQR    : () -> Unit,
) {
    val color = if (isRunning) ColorsSystem.Green else AccentPurple

    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .background(color, RoundedCornerShape(99.dp))
                    )
                    Text(
                        text          = if (isRunning) "SESSION ACTIVE" else "MODE MAÎTRE DE JEU",
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = color,
                        letterSpacing = 0.5.sp,
                        fontFamily    = NunitoFontFamily,
                    )
                }

                if (isRunning) {
                    TextButton(
                        onClick = onShowQR,
                        shape   = RoundedCornerShape(8.dp),
                        colors  = ButtonDefaults.textButtonColors(contentColor = AccentPurple),
                    ) {
                        Text(
                            text       = if (showQr) "🔗 Fermer QR" else "🔗 QR Code",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                        )
                    }
                }
            }

            if (isRunning && config != null) {
                Text(
                    text = "CODE DE SESSION",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorsSystem.TextDisabled,
                    letterSpacing = 1.sp,
                    fontFamily = NunitoFontFamily,
                )

                Text(
                    text = config.token,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentPurple,
                    letterSpacing = 4.sp,
                    fontFamily = NunitoFontFamily,
                )
                Text(
                    text      = "$playerCount joueur${if (playerCount > 1) "s" else ""} connecté${if (playerCount > 1) "s" else ""}",
                    fontSize  = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color     = ColorsSystem.TextSecondary,
                    fontFamily = NunitoFontFamily,
                    modifier  = Modifier.padding(top = 2.dp),
                )
            } else {
                Text(
                    text      = "Aucune session en cours",
                    fontSize  = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color     = ColorsSystem.TextSecondary,
                    fontFamily = NunitoFontFamily,
                )
            }
        }
    }
}
