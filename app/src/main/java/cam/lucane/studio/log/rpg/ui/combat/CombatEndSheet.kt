package cam.lucane.studio.log.rpg.ui.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.AvatarBox
import cam.lucane.studio.log.rpg.ui.combat.model.CombatParticipant
import cam.lucane.studio.log.rpg.ui.combat.model.CombatState
import cam.lucane.studio.log.rpg.ui.combat.model.CombatStatus
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/** Résumé de fin de combat. Les PJs restent responsables de leur propre fiche. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatEndSheet(state: CombatState, onDismiss: () -> Unit) {
    val koCount   = state.participants.count { it.status == CombatStatus.KO }
    val fledCount = state.participants.count { it.status == CombatStatus.FLED }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("🏁  Fin de combat", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = NunitoFontFamily)
            Text("Round ${state.round}  ·  $koCount KO  ·  $fledCount en fuite", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f), fontFamily = NunitoFontFamily)

            val pjs = state.linkedPJs
            if (pjs.isNotEmpty()) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(ColorsSystem.Green.copy(alpha = 0.1f)).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("BILAN PJ — INDICATIF", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.Green, letterSpacing = 1.sp, fontFamily = NunitoFontFamily)
                    Text("Chaque joueur reste responsable de sa fiche.", fontSize = 10.sp, color = Color.White.copy(alpha = 0.35f), fontFamily = NunitoFontFamily)
                    Spacer(Modifier.height(4.dp))
                    pjs.forEach { PjSummaryRow(it) }
                }
            }

            Button(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
            ) {
                Text("Terminer le combat", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = NunitoFontFamily)
            }
        }
    }
}

@Composable
private fun PjSummaryRow(pj: CombatParticipant) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AvatarBox(letter = pj.avatarLetter, color = pj.avatarColor, size = 28.dp, shape = RoundedCornerShape(9.dp), textColor = Color.White)
        Text(pj.name, modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, fontFamily = NunitoFontFamily)
        Text("${pj.currentHp} / ${pj.maxHp} PV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorsSystem.Red, fontFamily = NunitoFontFamily)
    }
}