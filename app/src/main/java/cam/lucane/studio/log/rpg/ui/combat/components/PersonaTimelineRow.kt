package cam.lucane.studio.log.rpg.ui.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.AvatarBox
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.combat.model.CombatState
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * Bande horizontale scrollable des personas actifs triés par initiative.
 * Utilisée en mode portrait. Tap → sélection du persona à afficher.
 */
@Composable
fun PersonaTimelineRow(state: CombatState, onSelectPersona: (CombatParticipant) -> Unit, modifier: Modifier = Modifier) {
    val personas  = state.sortedActive
    val currentId = state.currentParticipant?.id
    val listState = rememberLazyListState()

    LaunchedEffect(state.currentTurnIndex) {
        val idx = personas.indexOfFirst { it.id == currentId }
        if (idx >= 0) listState.animateScrollToItem(idx)
    }

    LazyRow(
        state                 = listState,
        modifier              = modifier.fillMaxWidth().background(ColorsSystem.BackgroundApp).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding        = PaddingValues(horizontal = 12.dp),
    ) {
        itemsIndexed(personas, key = { _, p -> p.id }) { index, persona ->
            val isCurrent = persona.id == currentId
            if (index == 0 && state.isEndOfRound) {
                RoundEndMarker(nextRound = state.round + 1)
                Spacer(Modifier.width(4.dp))
            }
            PersonaChip(persona = persona, isCurrent = isCurrent, onClick = { onSelectPersona(persona) })
        }
        item {
            Spacer(Modifier.width(4.dp))
            RoundEndMarker(nextRound = state.round + 1)
        }
    }
}

@Composable
private fun PersonaChip(persona: CombatParticipant, isCurrent: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, if (isCurrent) ColorsSystem.Green else ColorsSystem.Divider, RoundedCornerShape(12.dp))
            .background(if (isCurrent) ColorsSystem.GreenLight else ColorsSystem.BackgroundCard)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AvatarBox(letter = persona.avatarLetter, color = persona.avatarColor, size = 32.dp, shape = CircleShape, fontSize = 12)

        Text(persona.name, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)

        Text("Init. ${persona.effectiveInitiative}", fontSize = 7.sp, color = if (isCurrent) ColorsSystem.GreenDark else ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)

        Box(modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(ColorsSystem.RedLight)) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(persona.hpPercent).clip(CircleShape).background(ColorsSystem.Red))
        }
    }
}

@Composable
private fun RoundEndMarker(nextRound: Int) {
    Column(
        modifier            = Modifier.width(40.dp).height(88.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.width(1.dp).weight(1f).background(ColorsSystem.Yellow))
        Spacer(Modifier.height(4.dp))
        Text("R$nextRound", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Yellow, fontFamily = NunitoFontFamily)
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.width(1.dp).weight(1f).background(ColorsSystem.Yellow))
    }
}