package cam.lucane.studio.log.rpg.ui.combat.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/** Étiquette de section en capitales espacées — partagée dans tout le module combat. */
@Composable
fun SectionLabel(text: String) {
    Text(
        text          = text,
        fontSize      = 9.sp,
        fontWeight    = FontWeight.ExtraBold,
        color         = ColorsSystem.TextSecondary,
        letterSpacing = 1.2.sp,
        fontFamily    = NunitoFontFamily,
    )
}