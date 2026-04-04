package cam.lucane.studio.log.rpg.data.entity

import androidx.compose.ui.graphics.Color
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

/**
 * Couleurs d'accent disponibles pour un widget de caractéristique.
 * Utilise directement ColorsSystem pour rester cohérent avec le design system.
 */
enum class WidgetAccentColor(val main: Color, val light: Color) {
    PURPLE(ColorsSystem.Purple, ColorsSystem.PurpleLight),
    GREEN (ColorsSystem.Green,  ColorsSystem.GreenLight),
    BLUE  (ColorsSystem.Blue,   ColorsSystem.BlueLight),
    RED   (ColorsSystem.Red,    ColorsSystem.RedLight),
    ORANGE(ColorsSystem.Orange, ColorsSystem.OrangeLight),
    YELLOW(ColorsSystem.Yellow, ColorsSystem.YellowLight),
}
