package cam.lucane.studio.log.rpg.ui.components.common

import androidx.compose.foundation.BorderStroke
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
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun SessionBanner(
    label: String,
    subtitle: String,
    // ✨ Boutons optionnels — null = pas de boutons (cas PseudoScreen)
    onSwitch: (() -> Unit)? = null,
    switchLabel: String = "⇄ Changer le perso",
    onQuit: (() -> Unit)? = null,
    quitLabel: String = "✕ Quitter",
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ColorsSystem.Green.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, ColorsSystem.Green.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {

            // ── Label + point vert ────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    Modifier
                        .size(7.dp)
                        .background(ColorsSystem.Green, RoundedCornerShape(99.dp))
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorsSystem.Green,
                    letterSpacing = 0.5.sp,
                    fontFamily = NunitoFontFamily
                )
            }

            // ── Sous-titre ────────────────────────────────────────────
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily,
                modifier = Modifier.padding(top = 2.dp)
            )

            // ── Boutons (optionnels) ──────────────────────────────────
            if (onSwitch != null || onQuit != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    onSwitch?.let {
                        OutlinedButton(
                            onClick        = it,
                            modifier       = Modifier.weight(1f),
                            shape          = RoundedCornerShape(9.dp),
                            colors         = ButtonDefaults.outlinedButtonColors(contentColor = ColorsSystem.Green),
                            border         = BorderStroke(1.5.dp, ColorsSystem.Green.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(
                                text = switchLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = NunitoFontFamily
                            )
                        }
                    }
                    onQuit?.let {
                        OutlinedButton(
                            onClick        = it,
                            modifier       = Modifier.weight(1f),
                            shape          = RoundedCornerShape(9.dp),
                            colors         = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                            border         = BorderStroke(1.5.dp, AccentRed.copy(alpha = 0.3f)),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(
                                text = quitLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = NunitoFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}