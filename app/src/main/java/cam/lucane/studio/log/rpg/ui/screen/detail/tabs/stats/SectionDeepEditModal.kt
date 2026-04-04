package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

/**
 * Modal ouvert par un long-press sur le titre d'une section.
 * Permet d'éditer le titre et de supprimer la section.
 */
@Composable
fun SectionDeepEditModal(
    currentTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (newTitle: String) -> Unit,
    onDelete: () -> Unit,
) {
    var title by remember { mutableStateOf(currentTitle) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(ColorsSystem.BackgroundCard)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Modifier la section",
                fontSize   = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = ColorsSystem.TextPrimary,
            )

            CombatTextField(
                value         = title,
                onValueChange = { title = it },
                label         = "Titre de la section",
                modifier      = Modifier.fillMaxWidth(),
            )

            // Confirmer
            Button(
                onClick  = { onConfirm(title) },
                enabled  = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
            ) {
                Text("Enregistrer", fontWeight = FontWeight.ExtraBold)
            }

            // Supprimer
            TextButton(
                onClick  = onDelete,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "🗑 Supprimer la section",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = ColorsSystem.Red,
                )
            }
        }
    }
}