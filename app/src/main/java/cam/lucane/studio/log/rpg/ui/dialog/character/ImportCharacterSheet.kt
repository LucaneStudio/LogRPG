package cam.lucane.studio.log.rpg.ui.dialog.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.navigation.Routes
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun ImportCharacterSheet(
    navController: NavHostController,
    onDismiss: () -> Unit,
    onPickJson: () -> Unit,
) {
    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "📥 Importer un personnage",
        subtitle = "Choisissez votre source"
    ) {
        // ── Scanner QR ──
        ShareActionRow(
            emoji            = "📷",
            emojiBackground  = ColorsSystem.PurpleLight,
            label            = "Scanner les QR Codes",
            description      = "Scanner 1 à 4 QR · Stats obligatoire · Reste optionnel",
            onClick          = { onDismiss(); navController.navigate(Routes.MULTI_QR_SCAN) }
        )

        // ── Divider ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(ColorsSystem.Divider)
        )

        // ── Fichier JSON ──
        ImportActionRow(
            emoji = "📄",
            emojiBackground = ColorsSystem.BlueLight,
            label = "Depuis un fichier JSON",
            description = "Ouvrir depuis les fichiers ou téléchargements",
            onClick = { onDismiss(); onPickJson() }
        )

        Spacer(Modifier.height(8.dp))

        // ── Annuler ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(99.dp))
                .background(ColorsSystem.BackgroundSurface)
                .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                .clickable { onDismiss() }
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Annuler", fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary
            )
        }
    }
}

@Composable
private fun ImportActionRow(
    emoji: String,
    emojiBackground: androidx.compose.ui.graphics.Color,
    label: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(emojiBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary
            )
            Text(
                description, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled,
                modifier = Modifier.padding(top = 1.dp)
            )
        }

        Text("›", fontSize = 20.sp, color = ColorsSystem.TextDisabled,
            fontWeight = FontWeight.Light)
    }
}
