package cam.lucane.studio.log.rpg.ui.screen.qr

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterListViewModel
import cam.lucane.studio.log.rpg.utils.MultiQrCodeUtils

@Composable
fun MultiQrImportPreviewScreen(
    statsRaw: String,
    abilitiesRaw: String?,
    itemsRaw: String?,
    notesRaw: String?,
    viewModel: CharacterListViewModel,
    onImported: (Long) -> Unit,
    onBack: () -> Unit
) {
    // Décoder les payloads
    val stats = remember { MultiQrCodeUtils.decodeStats(statsRaw) }
    val abilities = remember { abilitiesRaw?.let { MultiQrCodeUtils.decodeAbilities(it) } }
    val items = remember { itemsRaw?.let { MultiQrCodeUtils.decodeItems(it) } }
    val notes = remember { notesRaw?.let { MultiQrCodeUtils.decodeNotes(it) } }

    val isPartial = abilitiesRaw == null || itemsRaw == null || notesRaw == null
    val skippedCount = listOf(abilitiesRaw, itemsRaw, notesRaw).count { it == null }

    if (stats == null) {
        // Payload stats corrompu
        Box(
            modifier = Modifier.fillMaxSize().background(ColorsSystem.BackgroundApp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text("❌", fontSize = 48.sp)
                Text("QR Stats invalide ou corrompu",
                    fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.Red,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(99.dp))
                        .background(ColorsSystem.BackgroundSurface)
                        .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                        .clickable { onBack() }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Retour", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().background(ColorsSystem.BackgroundApp)
    ) {
        // TopBar
        Row(
            modifier = Modifier.fillMaxWidth().background(ColorsSystem.BackgroundCard)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp)
                    .background(ColorsSystem.BackgroundCard, RoundedCornerShape(12.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", fontSize = 18.sp, color = ColorsSystem.TextSecondary) }
            Column {
                Text("Personnage détecté", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
                Text(
                    if (isPartial) "${4 - skippedCount} scannés · $skippedCount non importé${if (skippedCount > 1) "s" else ""}"
                    else "4 / 4 codes scannés · Import complet",
                    fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Badge succès ou partiel
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(if (isPartial) ColorsSystem.YellowLight else ColorsSystem.GreenLight)
                    .border(1.5.dp,
                        if (isPartial) ColorsSystem.Yellow.copy(.4f) else ColorsSystem.Green.copy(.3f),
                        RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp))
                        .background(if (isPartial) ColorsSystem.Yellow else ColorsSystem.GreenDark),
                    contentAlignment = Alignment.Center
                ) { Text(if (isPartial) "⚠️" else "✅", fontSize = 17.sp) }
                Column {
                    Text(
                        if (isPartial) "Import partiel" else "4 / 4 codes scannés",
                        fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily,
                        color = if (isPartial) ColorsSystem.YellowDark else ColorsSystem.GreenDark
                    )
                    if (isPartial) {
                        Text("${skippedCount} section${if (skippedCount > 1) "s" else ""} non importée${if (skippedCount > 1) "s" else ""}",
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
                    } else {
                        Text("Données complètes · Format optimisé",
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
                    }
                }
            }

            // Pills état de chaque section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SectionPill("Stats", true, true)
                SectionPill("Sorts", abilitiesRaw != null, abilitiesRaw != null)
                SectionPill("Inventaire", itemsRaw != null, itemsRaw != null)
                SectionPill("Notes", notesRaw != null, notesRaw != null)
            }

            // Card aperçu
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(ColorsSystem.BackgroundCard),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Header perso
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(15.dp))
                            .background(ColorsSystem.GradientAvatarPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stats.name.first().uppercaseChar().toString(),
                            fontSize = 20.sp, fontWeight = FontWeight.Black,
                            fontFamily = NunitoFontFamily, color = ColorsSystem.BackgroundCard)
                    }
                    Column {
                        Text(stats.name, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
                        Text(if (isPartial) "Import partiel" else "Import complet",
                            fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold,
                            fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
                    }
                }

                Divider()
                StatRow("❤️ Points de vie", "${stats.maxHealth} max", true)
                Divider()
                StatRow("💧 Mana", "${stats.maxMana} max", true)

                // Slots si présents
                if (stats.spellSlots.isNotEmpty()) {
                    Divider()
                    StatRow("📖 Emplacements",
                        "${stats.spellSlots.count { it.max > 0 }} niveaux", true)
                }

                Divider()
                StatRowPill("📖 Capacités",
                    abilities?.size?.let { "$it sorts" },
                    abilitiesRaw != null,
                    ColorsSystem.Purple, ColorsSystem.PurpleLight)

                Divider()
                StatRowPill("🎒 Inventaire",
                    items?.size?.let { "$it objets" },
                    itemsRaw != null,
                    ColorsSystem.Orange, ColorsSystem.OrangeLight)

                Divider()
                StatRowPill("📝 Notes",
                    notes?.size?.let { "$it notes" },
                    notesRaw != null,
                    ColorsSystem.Green, ColorsSystem.GreenLight)

                Divider()
                StatRow("🪙 Monnaie", "${stats.credits} crédits", true)
            }
        }

        // Footer
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(99.dp))
                    .background(ColorsSystem.GradientGreen)
                    .clickable {
                        viewModel.importFromMultiQr(
                            stats = stats,
                            abilities = abilities,
                            items = items,
                            notes = notes,
                            onSuccess = { id -> onImported(id) },
                            onError = {}
                        )
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("✅ Importer ${stats.name}",
                    fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.BackgroundCard)
            }
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(99.dp))
                    .background(ColorsSystem.BackgroundSurface)
                    .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                    .clickable { onBack() }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Annuler", fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
            }
        }
    }
}

@Composable
private fun RowScope.SectionPill(label: String, scanned: Boolean, available: Boolean) {
    val bg = when {
        scanned -> ColorsSystem.GreenLight
        !available -> ColorsSystem.RedLight
        else -> ColorsSystem.BackgroundSurface
    }
    val color = when {
        scanned -> ColorsSystem.GreenDark
        !available -> ColorsSystem.Red
        else -> ColorsSystem.TextDisabled
    }
    val icon = when {
        scanned -> "✓"
        !available -> "—"
        else -> "·"
    }
    Box(
        modifier = Modifier.weight(1f).clip(RoundedCornerShape(99.dp))
            .background(bg).padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("$icon $label", fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = NunitoFontFamily, color = color)
    }
}

@Composable
private fun StatRow(label: String, value: String, available: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp)
            .then(if (!available) Modifier.graphicsLayer { alpha = .4f } else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
        Text(value, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
    }
}

@Composable
private fun StatRowPill(
    label: String, value: String?, available: Boolean,
    pillColor: androidx.compose.ui.graphics.Color,
    pillBg: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp)
            .then(if (!available) Modifier.graphicsLayer { alpha = .4f } else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
        if (available && value != null) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(pillBg)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(value, fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = pillColor)
            }
        } else {
            Text("— non importé", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.Red.copy(.6f))
        }
    }
}

@Composable
private fun Divider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.5.dp)
        .background(ColorsSystem.Divider))
}
