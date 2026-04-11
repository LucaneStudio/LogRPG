package cam.lucane.studio.log.rpg.ui.screen.mj.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import cam.lucane.studio.log.rpg.data.entity.getSpellSlots
import cam.lucane.studio.log.rpg.data.entity.toJson
import cam.lucane.studio.log.rpg.data.session.PlayerSlot
import cam.lucane.studio.log.rpg.ui.components.common.ProfileImage
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.screen.list.components.bar.HealthManaBar
import cam.lucane.studio.log.rpg.ui.screen.list.components.bar.SpellSlotsBar
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.utils.getAccentBrushByCharacterId
import cam.lucane.studio.log.rpg.ui.utils.getAccentColorByCharacterId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun MJPlayerCard(
    slot: PlayerSlot,
    onKick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = remember(slot.playerName) {
        getAccentColorByCharacterId(slot.playerName.hashCode().toLong().and(0x7FFFFFFF))
    }
    val accentBrush = remember(slot.playerName) {
        getAccentBrushByCharacterId(slot.playerName.hashCode().toLong().and(0x7FFFFFFF))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (slot.isConnected) 1f else 0.45f)
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorsSystem.BackgroundCard,
            contentColor = accentColor
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Infos ─────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        // Nom joueur (petit, au-dessus)
                        Text(
                            text = slot.playerName.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorsSystem.TextDisabled,
                            letterSpacing = 1.sp,
                            fontFamily = NunitoFontFamily
                        )
                        // Nom personnage (grand, principal)
                        Text(
                            text = if (slot.isConnected)
                                slot.info?.characterName ?: "—"
                            else "Déconnecté",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (slot.isConnected) ColorsSystem.TextPrimary
                            else ColorsSystem.TextDisabled,
                            fontFamily = NunitoFontFamily
                        )
                    }

                    // Bouton kick
                    CardOptionButton(
                        modifier = Modifier.size(26.dp),
                        onClick  = onKick,
                        color    = accentColor
                    ) {
                        Text(
                            text       = "✕",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Black,
                            color      = ColorsSystem.Red,
                            fontFamily = NunitoFontFamily
                        )
                    }
                }

                Spacer(Modifier.size(5.dp))

                // ── Barres PV / Mana / Sorts ──────────────────────────
                slot.info?.let { info ->
                    Row {
                        HealthManaBar(
                            modifier = Modifier.weight(1f),
                            label   = "❤\uFE0F",
                            current = info.currentHealth,
                            max     = info.maxHealth,
                            color   = ColorsSystem.Red
                        ){
                            Text(
                                text = "${info.currentHealth} / ${info.maxHealth}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ColorsSystem.Red,
                                fontFamily = NunitoFontFamily,
                                textAlign = TextAlign.End,
                                modifier = Modifier.defaultMinSize(minWidth = 50.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    if (info.hasMana) {
                        Row {
                            HealthManaBar(
                                modifier = Modifier.weight(1f),
                                label   = "\uD83D\uDCA7",
                                current = info.currentMana,
                                max     = info.maxMana,
                                color   = ColorsSystem.Blue
                            ){
                                Text(
                                    text = "${info.currentMana} / ${info.maxMana}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ColorsSystem.Blue,
                                    fontFamily = NunitoFontFamily,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.defaultMinSize(minWidth = 50.dp)
                                )
                            }
                        }

                    } else {
                        // Mode emplacements de sorts
                        val activeSlots = remember(info.spellSlotsJson) {
                            parseSpellSlots(info.spellSlotsJson).filter { it.max > 0 }
                        }
                        if (activeSlots.isNotEmpty()) {
                            SpellSlotsBar(
                                label = "📖",
                                slots = activeSlots
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Parser spell slots depuis le JSON reçu ────────────────────────────────

private fun parseSpellSlots(json: String?): List<SpellSlot> {
    if (json == null) return emptyList()
    return runCatching {
        Gson().fromJson(json, Array<SpellSlot>::class.java).toList()
    }.getOrElse { emptyList() }
}