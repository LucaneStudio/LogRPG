package cam.lucane.studio.log.rpg.ui.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.combat.model.SpellSlotDisplay
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

// SectionLabel est dans SectionLabel.kt (même package)

@Composable
fun ParticipantStatsSection(participant: CombatParticipant, onHpChange: (delta: Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HpBlock(participant, onHpChange)
        if (participant.hasMana)
            ManaBlock(participant)
        else if (participant.spellSlots.isNotEmpty())
            SpellSlotsBlock(participant.spellSlots)
    }
}

@Composable
private fun HpBlock(p: CombatParticipant, onHpChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            SectionLabel("❤️  POINTS DE VIE")
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(p.currentHp.toString(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.RedDark, fontFamily = NunitoFontFamily, lineHeight = 28.sp)
                Text("/ ${p.maxHp}", fontSize = 11.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(ColorsSystem.RedLight)) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(p.hpPercent).clip(CircleShape).background(ColorsSystem.Red))
        }
        if (p.isReadOnly) {
            Text("Géré par le joueur", fontSize = 9.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf(-5, -1, +1, +5).forEach { delta ->
                    Button(
                        onClick        = { onHpChange(delta) },
                        modifier       = Modifier.weight(1f).height(32.dp),
                        shape          = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors         = ButtonDefaults.buttonColors(containerColor = ColorsSystem.RedLight),
                    ) {
                        Text(if (delta > 0) "+$delta" else "$delta", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.RedDark, fontFamily = NunitoFontFamily)
                    }
                }
            }
        }
    }
}

@Composable
private fun ManaBlock(p: CombatParticipant) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SectionLabel("💧  MANA")
            Text("${p.currentMana} / ${p.maxMana}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Blue, fontFamily = NunitoFontFamily)
        }
        Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(ColorsSystem.BlueLight)) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(p.manaPercent).clip(CircleShape).background(ColorsSystem.Blue))
        }
        Text("Géré par le joueur", fontSize = 9.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
    }
}

@Composable
private fun SpellSlotsBlock(slots: List<SpellSlotDisplay>) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        SectionLabel("📖  EMPLACEMENTS DE SORTS")
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            slots.forEach { slot ->
                val active = slot.remaining > 0
                Text(
                    text       = "N${slot.level} ${slot.remaining}",
                    fontSize   = 9.sp, fontWeight = FontWeight.ExtraBold,
                    color      = if (active) ColorsSystem.Purple else ColorsSystem.TextDisabled,
                    fontFamily = NunitoFontFamily,
                    modifier   = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(if (active) ColorsSystem.PurpleLight else ColorsSystem.BackgroundSurface)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
        }
        Text("Géré par le joueur", fontSize = 9.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
    }
}