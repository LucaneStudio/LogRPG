package cam.lucane.studio.log.rpg.ui.utils

import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

fun getAccentColorByCharacterId(id: Long): Color{
    val colors = listOf(
        ColorsSystem.Purple,
        ColorsSystem.Pink,
        ColorsSystem.Yellow,
        ColorsSystem.Orange,
        ColorsSystem.Green
    )

    return colors[(id % colors.size).toInt()]
}

fun getAccentBrushByCharacterId(id: Long): Brush{
    val colors = listOf(
        ColorsSystem.GradientAvatarPurple,
        ColorsSystem.GradientAvatarPink,
        ColorsSystem.GradientAvatarYellow,
        ColorsSystem.GradientAvatarOrange,
        ColorsSystem.GradientAvatarGreen
    )

    return colors[(id % colors.size).toInt()]
}