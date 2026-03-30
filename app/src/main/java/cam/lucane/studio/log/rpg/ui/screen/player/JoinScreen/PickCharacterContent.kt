package cam.lucane.studio.log.rpg.ui.screen.player.JoinScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.components.common.SessionBanner
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun PickCharacterContent(
    characters: List<Character>,
    playerName: String,
    onPick: (Character) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
            .padding(16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        SessionBanner(
            label    = "SESSION REJOINTE",
            subtitle = "Connecté en tant que $playerName"
        )

        Text(
            text = "QUEL PERSONNAGE PARTAGER ?",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorsSystem.TextDisabled,
            letterSpacing = 1.5.sp,
            fontFamily = NunitoFontFamily
        )
        Text(
            text = "Le MJ verra ses PV et mana en temps réel.\nVous pouvez naviguer librement sans changer le partage.",
            fontSize = 11.sp,
            color = ColorsSystem.TextSecondary,
            fontFamily = NunitoFontFamily,
            lineHeight = 16.sp
        )

        // ── Liste personnages ─────────────────────────────────────────
        Card(
            shape  = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ColorsSystem.BackgroundCard)
        ) {
            characters.forEachIndexed { idx, character ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(character) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar initiale
                    Text(
                        text = character.name[0].toString(),
                        modifier = Modifier
                            .size(48.dp)
                            .background(AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .wrapContentSize(Alignment.Center),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentPurple,
                        fontFamily = NunitoFontFamily
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = character.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorsSystem.TextPrimary,
                            fontFamily = NunitoFontFamily
                        )
                        Text(
                            text = "${character.currentHealth}/${character.maxHealth} PV",
                            fontSize = 11.sp,
                            color = ColorsSystem.TextSecondary,
                            fontFamily = NunitoFontFamily
                        )
                    }
                    Text(
                        text = "›",
                        fontSize = 20.sp,
                        color = ColorsSystem.TextDisabled
                    )
                }
                if (idx < characters.lastIndex) {
                    HorizontalDivider(
                        color = ColorsSystem.Divider,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick  = onCancel,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
            border   = BorderStroke(1.5.dp, AccentRed.copy(alpha = 0.4f))
        ) {
            Text(
                text = "Annuler",
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily
            )
        }
    }
}