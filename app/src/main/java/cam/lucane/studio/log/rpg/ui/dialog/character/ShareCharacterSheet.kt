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
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun ShareCharacterSheet(
    character: Character,
    estimatedQrSize: Int,          // depuis viewModel.estimateQrSize()
    onDismiss: () -> Unit,
    onMultiQr: () -> Unit,
    onShareQrNoNotes: () -> Unit,  // fallback si trop lourd
    onShareJson: () -> Unit
) {
    val qrLimit = 2953
    val isQrOk = estimatedQrSize <= qrLimit

    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "📤 Partager le personnage",
        subtitle = "Choisissez le format d'export"
    ) {
        // ── Badge personnage ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ColorsSystem.BackgroundSurface)
                .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorsSystem.GradientAvatarPurple),  // ou selon le perso
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.name.first().uppercaseChar().toString(),
                    fontSize = 14.sp, fontWeight = FontWeight.Black,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.BackgroundCard
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(character.name, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
                Text("~${estimatedQrSize / 1024}.${(estimatedQrSize % 1024) / 100} KB",
                    fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
            }

            // Badge taille
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (isQrOk) ColorsSystem.GreenLight else ColorsSystem.OrangeLight)
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isQrOk) "✓ QR possible" else "⚠ Trop lourd",
                    fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily,
                    color = if (isQrOk) ColorsSystem.GreenDark else ColorsSystem.Orange
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Option QR ──
        if (isQrOk) {
            ShareActionRow(
                emoji            = "🔲",
                emojiBackground  = ColorsSystem.PurpleLight,
                label            = "Multi-QR Codes",
                description      = "4 QR légers · Tout le personnage · Recommandé",
                onClick          = { onDismiss(); onMultiQr() }
            )

        } else {
            // QR désactivé + fallback sans notes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorsSystem.BackgroundSurface)
                    .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp))
                            .background(ColorsSystem.Divider),
                        contentAlignment = Alignment.Center
                    ) { Text("📷", fontSize = 20.sp) }
                    Column {
                        Text("QR Code", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                            color = ColorsSystem.TextDisabled)
                        Text("Non disponible pour ce personnage", fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold, fontFamily = NunitoFontFamily,
                            color = ColorsSystem.TextDisabled)
                    }
                }

                // Bloc orange explication + fallback
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorsSystem.OrangeLight)
                        .border(1.5.dp, ColorsSystem.Orange.copy(.25f), RoundedCornerShape(10.dp))
                        .padding(11.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⚠️ Personnage trop volumineux",
                        fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily, color = ColorsSystem.Orange)
                    Text("Les notes longues dépassent la limite du QR. Vous pouvez exporter sans les notes :",
                        fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold,
                        fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary,
                        lineHeight = 15.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(99.dp))
                            .background(ColorsSystem.Orange)
                            .clickable { onDismiss(); onShareQrNoNotes() }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📷 Exporter sans les notes",
                            fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily, color = ColorsSystem.BackgroundCard)
                    }
                }
            }
        }

        Spacer(Modifier.height(2.dp))

        // ── Divider ──
        Box(modifier = Modifier.fillMaxWidth().height(1.5.dp).background(ColorsSystem.Divider))

        Spacer(Modifier.height(2.dp))

        // ── Option JSON ──
        ShareActionRow(
            emoji = "📄",
            emojiBackground = ColorsSystem.BlueLight,
            label = "Fichier JSON",
            description = "Envoyer via WhatsApp, Drive, mail…",
            onClick = { onDismiss(); onShareJson() }
        )

        Spacer(Modifier.height(8.dp))

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
            Text("Annuler", fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
        }
    }
}

@Composable
internal fun ShareActionRow(
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
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp))
                .background(emojiBackground),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 20.sp) }

        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
            Text(description, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled,
                modifier = Modifier.padding(top = 1.dp))
        }

        Text("›", fontSize = 20.sp, color = ColorsSystem.TextDisabled,
            fontWeight = FontWeight.Light)
    }
}
