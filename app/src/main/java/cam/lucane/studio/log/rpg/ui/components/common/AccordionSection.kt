package cam.lucane.studio.log.rpg.ui.components.common

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * Section dépliable sans bordure extérieure.
 * Fond BackgroundCard, séparateur interne uniquement quand ouverte.
 * Utilisable partout dans l'app.
 *
 * @param title   Titre de la section
 * @param subtitle Sous-titre (compteur, état…)
 * @param expanded Vrai si la section est ouverte
 * @param onToggle Callback au tap sur l'en-tête
 * @param content  Contenu affiché quand expanded = true
 */
@Composable
fun AccordionSection(
    title   : String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content : @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColorsSystem.BackgroundCard),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily)
                Text(subtitle, fontSize = 10.sp, color = ColorsSystem.TextSecondary, fontFamily = NunitoFontFamily)
            }
            Text(
                if (expanded) "▲" else "▼",
                fontSize   = 11.sp,
                fontWeight = FontWeight.Bold,
                color      = if (expanded) ColorsSystem.Green else ColorsSystem.TextDisabled,
                fontFamily = NunitoFontFamily,
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut(),
        ) {
            Column {
                HorizontalDivider(color = ColorsSystem.Divider)
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = content,
                )
            }
        }
    }
}