package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.template.CharacterTemplate
import cam.lucane.studio.log.rpg.data.template.CharacterTemplates
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * Bottom sheet de sélection d'un template de caractéristiques.
 * Affiché depuis [StatsTab] via le bouton "Depuis un template".
 *
 * @param hasSections  vrai si des sections existent déjà → affiche un avertissement.
 * @param onConfirm    appelé avec le template choisi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePickerSheet(
    hasSections: Boolean,
    onDismiss  : () -> Unit,
    onConfirm  : (CharacterTemplate) -> Unit,
) {
    var selected by remember { mutableStateOf<CharacterTemplate?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = ColorsSystem.BackgroundCard,
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 14.dp)
                    .width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(ColorsSystem.SecondBorder)
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // ── En-tête ───────────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Choisir un template",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = ColorsSystem.TextPrimary,
                    fontFamily = NunitoFontFamily,
                )
                Text(
                    "Les sections et widgets seront ajoutés à la suite des existants.",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color      = ColorsSystem.TextSecondary,
                    fontFamily = NunitoFontFamily,
                )
            }

            // ── Avertissement si sections déjà présentes ──────────────────────
            if (hasSections) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorsSystem.OrangeLight)
                        .border(1.dp, ColorsSystem.Orange.copy(.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text("⚠️", fontSize = 14.sp)
                    Text(
                        "Des sections existent déjà. Le template sera ajouté en dessous.",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = ColorsSystem.Orange,
                        fontFamily = NunitoFontFamily,
                    )
                }
            }

            // ── Liste des templates ───────────────────────────────────────────
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier            = Modifier.heightIn(max = 420.dp),
            ) {
                items(CharacterTemplates.all, key = { it.id }) { template ->
                    TemplateCard(
                        template   = template,
                        isSelected = selected?.id == template.id,
                        onClick    = { selected = template },
                    )
                }
            }

            // ── Bouton confirmer ──────────────────────────────────────────────
            Button(
                onClick  = { selected?.let(onConfirm) },
                enabled  = selected != null,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(99.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
            ) {
                Text(
                    "Appliquer le template",
                    fontWeight = FontWeight.ExtraBold,
                    modifier   = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

// ── Carte individuelle ────────────────────────────────────────────────────────

@Composable
private fun TemplateCard(
    template  : CharacterTemplate,
    isSelected: Boolean,
    onClick   : () -> Unit,
) {
    val shape       = RoundedCornerShape(14.dp)
    val borderColor = if (isSelected) ColorsSystem.Green else ColorsSystem.Divider
    val bgColor     = if (isSelected) ColorsSystem.GreenLight else ColorsSystem.BackgroundSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .border(1.5.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Emoji dans un cercle de fond
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ColorsSystem.BackgroundCard),
            contentAlignment = Alignment.Center,
        ) {
            Text(template.emoji, fontSize = 22.sp)
        }

        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text       = template.name,
                fontSize   = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = if (isSelected) ColorsSystem.GreenDark else ColorsSystem.TextPrimary,
                fontFamily = NunitoFontFamily,
            )
            Text(
                text       = template.description,
                fontSize   = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color      = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily,
                lineHeight = 14.sp,
            )
            // Aperçu des sections
            Text(
                text       = template.sections.joinToString(" · ") { it.title },
                fontSize   = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = if (isSelected) ColorsSystem.Green else ColorsSystem.TextDisabled,
                fontFamily = NunitoFontFamily,
                letterSpacing = 0.5.sp,
            )
        }

        if (isSelected) {
            Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ColorsSystem.Green)
        }
    }
}