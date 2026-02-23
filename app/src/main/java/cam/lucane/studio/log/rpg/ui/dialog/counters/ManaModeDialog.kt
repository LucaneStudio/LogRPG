package cam.lucane.studio.log.rpg.ui.dialog.counters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.ManaMode
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetDivider
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun ManaModeDialog(
    currentMode: ManaMode,
    onDismiss: () -> Unit,
    onConfirm: (ManaMode) -> Unit
) {
    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "Mode du compteur"
    ) {
        data class Option(val mode: ManaMode, val emoji: String, val label: String, val desc: String)

        val options = listOf(
            Option(ManaMode.MANA,        "💧", "Mana",                  "Barre classique avec +/−"),
            Option(ManaMode.SPELL_SLOTS, "📖", "Emplacements de sorts", "Grille par niveau, tap pour utiliser"),
        )

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            options.forEachIndexed { i, opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onConfirm(opt.mode) }
                        .padding(horizontal = 8.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Indicateur radio
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (opt.mode == currentMode) ColorsSystem.Blue else Color.Transparent)
                            .border(
                                2.5.dp,
                                if (opt.mode == currentMode) ColorsSystem.Blue else ColorsSystem.TextDisabled,
                                CircleShape
                            )
                    )

                    // Icône
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(ColorsSystem.BlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(opt.emoji, fontSize = 18.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opt.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                            color = ColorsSystem.TextPrimary
                        )
                        Text(
                            text = opt.desc,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NunitoFontFamily,
                            color = ColorsSystem.TextDisabled
                        )
                    }
                }
                if (i < options.size - 1) SheetDivider()
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(99.dp))
                .background(ColorsSystem.BackgroundSurface)
                .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                .clickable { onDismiss() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fermer",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color = ColorsSystem.TextSecondary
            )
        }
    }
}
