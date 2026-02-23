package cam.lucane.studio.log.rpg.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Palette principale ──────────────────────────────────────────────────────
val BackgroundDark    = Color(0xFF0D0F1A)
val SurfaceDark       = Color(0xFF13162A)
val SurfaceVariant    = Color(0xFF1A1D30)

val AccentPurple      = Color(0xFF7C6AF7)
val AccentPurpleLight = Color(0xFFC4B8FF)
val AccentRed         = Color(0xFFF7716A)
val AccentGreen       = Color(0xFF5DE8C1)
val AccentGold        = Color(0xFFF59E0B)
val AccentCopper       = Color(0xFFB57434)

val HealthRed         = Color(0xFFEF4444)
val ManaBlue          = Color(0xFF6366F1)

val TextPrimary       = Color(0xFFE8E6F0)
val TextSecondary     = Color(0xFF8B899A)

val BorderSubtle      = Color(0x14FFFFFF)   // 8% blanc
val GlassSurface      = Color(0x0AFFFFFF)   // 4% blanc

// ── Scheme Material3 ────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = AccentPurple,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF2D2660),
    onPrimaryContainer = AccentPurpleLight,

    secondary        = AccentGreen,
    onSecondary      = Color(0xFF002B21),
    secondaryContainer = Color(0xFF003828),
    onSecondaryContainer = AccentGreen,

    tertiary         = AccentRed,
    onTertiary       = Color.White,

    background       = BackgroundDark,
    onBackground     = TextPrimary,

    surface          = SurfaceDark,
    onSurface        = TextPrimary,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline          = BorderSubtle,
    outlineVariant   = Color(0x1FFFFFFF),

    error            = HealthRed,
    onError          = Color.White,
)

@Composable
fun LogRPGTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography   = Typography,
        content      = content
    )
}