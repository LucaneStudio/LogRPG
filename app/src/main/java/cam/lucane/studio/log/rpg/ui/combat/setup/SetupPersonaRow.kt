package cam.lucane.studio.log.rpg.ui.combat.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun SetupPersonaRow(
    participants: List<CombatParticipant>,
    onRemove    : (String) -> Unit,
    modifier    : Modifier = Modifier,
) {
    participants.sortedBy { it.initiative }
    if (participants.isEmpty()) {
        EmptyPersonaRow(modifier)
        return
    }
    LazyRow(
        modifier              = modifier.fillMaxWidth().background(ColorsSystem.BackgroundCard).padding(vertical = 8.dp),
        contentPadding        = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(participants, key = { it.id }) { persona ->
            PersonaChip(persona = persona, onRemove = { onRemove(persona.id) })
        }
    }
}

@Composable
private fun PersonaChip(persona: CombatParticipant, onRemove: () -> Unit) {
    val hasInit = persona.initiative != 0
    Column(
        modifier = Modifier
            .width(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (hasInit) ColorsSystem.GreenLight else ColorsSystem.TextDisabled.copy(0.2f))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(persona.avatarColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(persona.avatarLetter, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.BackgroundCard, fontFamily = NunitoFontFamily)
        }
        Text(persona.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorsSystem.TextPrimary, fontFamily = NunitoFontFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(
            if (hasInit) "Init. ${persona.initiative}" else "Init. —",
            fontSize   = 10.sp,
            color      = if (hasInit) ColorsSystem.GreenDark else ColorsSystem.TextSecondary,
            fontFamily = NunitoFontFamily,
        )
    }
}

@Composable
private fun EmptyPersonaRow(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ColorsSystem.BackgroundSurface),
        contentAlignment = Alignment.Center,
    ) {
        Text("Aucun participant ajouté", fontSize = 12.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
    }
}