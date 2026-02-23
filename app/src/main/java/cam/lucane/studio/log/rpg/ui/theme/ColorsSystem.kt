package cam.lucane.studio.log.rpg.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object ColorsSystem {
    val BackgroundExterior = Color(0xFFC8DDD9)
    val BackgroundApp = Color(0xFFEEF6F4)
    val BackgroundCard = Color(0xFFFFFFFF)
    val BackgroundSurface = Color(0xFFF7FCFB)
    val PhoneBorder = Color(0xFFB8CCC9)
    val Divider = Color(0xFFEEF2F5)
    val SecondBorder = Color(0xFFE0E8EC)

    // Text
    val TextPrimary = Color(0xFF2C3E50)
    val TextSecondary = Color(0xFF6B7C8E)
    val TextDisabled = Color(0xFFA8B8C8)

    // Green
    val Green = Color(0xFF5CC8A8)
    val GreenLight = Color(0xFFD4F2EA)
    val GreenDark = Color(0xFF3DAF8E)

    // Red
    val RedDark = Color(0xFFE03030)
    val Red = Color(0xFFFF6B6B)
    val RedLight = Color(0xFFFFE0E0)
    val RedExtraBar = Color(0xFFFFBBBB)

    // Blue
    val Blue = Color(0xFF5B9CF6)
    val BlueLight = Color(0xFFDDEAFF)

    //Cyan
    val Cyan = Color(0xFF66E8E8)

    // Purple
    val Purple = Color(0xFFA78BFA)
    val PurpleLight = Color(0xFFEDE9FF)

    // Pink
    val Pink = Color(0xFFFF7EB3)
    val PinkLight = Color(0xFFFFE0EF)

    // Yellow
    val Yellow = Color(0xFFFFD166)
    val YellowLight = Color(0xFFFFF3CC)

    // Orange
    val Orange = Color(0xFFFF8C42)
    val OrangeLight = Color(0xFFFFE5D0)

    // Shadows (avec alpha)
    val Shadow = Color(0xFF5A8C78)

    val GradientAvatarPurple = Brush.linearGradient(
        colors = listOf(Color(0xFFA78BFA), Color(0xFF7C5CDB)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val GradientAvatarGreen = Brush.linearGradient(
        colors = listOf(Color(0xFF5CC8A8), Color(0xFF3DAF8E)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val GradientAvatarOrange = Brush.linearGradient(
        colors = listOf(Color(0xFFFF8C42), Color(0xFFE06020)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val GradientAvatarYellow = Brush.linearGradient(
        colors = listOf(Color(0xFFFFD166), Color(0xFFE6A800)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val GradientAvatarPink = Brush.linearGradient(
        colors = listOf(Color(0xFFFF7EB3), Color(0xFFE0508A)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // ── Logo / FAB ──
    val GradientGreen = Brush.linearGradient(
        colors = listOf(Color(0xFF5CC8A8), Color(0xFF3DAF8E)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // ── Barres de vie / mana ──
    val GradientBarHealth = Brush.linearGradient(
        colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF9999)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)  // horizontal
    )
    val GradientTempBarHealth = Brush.linearGradient(
        colors = listOf(Color(0xFFFFBBBB), Color(0xFFFFD5D5)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)  // horizontal
    )
    val GradientBarMana = Brush.linearGradient(
        colors = listOf(Color(0xFF5B9CF6), Color(0xFF99BBFF)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)  // horizontal
    )

    // ── Bouton lier PDF ──
    val GradientButtonPrimary = Brush.linearGradient(
        colors = listOf(Color(0xFF5CC8A8), Color(0xFF3DAF8E)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // ── Bouton Valider (modal sorts) ──
    val GradientButtonPurple = Brush.linearGradient(
        colors = listOf(Color(0xFFA78BFA), Color(0xFF7C5CDB)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}