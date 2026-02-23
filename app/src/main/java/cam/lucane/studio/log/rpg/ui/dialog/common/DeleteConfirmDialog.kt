package cam.lucane.studio.log.rpg.ui.dialog.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

/**
 * Modale de confirmation de suppression générique.
 * Utilisée pour les objets, sorts, notes, etc.
 */
@Composable
fun DeleteConfirmDialog(
    emoji: String,
    title: String,
    subtitle: String,
    confirmLabel: String = "Supprimer",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BaseBottomSheet(
        onDismiss = onDismiss,
        title = title,
        subtitle = subtitle
    ) {
        // Icône centrale
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ColorsSystem.RedLight),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 28.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        SheetButtonRow(
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            confirmLabel = confirmLabel,
            isDanger = true
        )
    }
}

// ── Raccourcis spécifiques ────────────────────────────────────────────────

@Composable
fun DeleteItemDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) = DeleteConfirmDialog(
    emoji = "🗑",
    title = "Supprimer l'objet ?",
    subtitle = "\"$itemName\" sera supprimé définitivement.",
    onDismiss = onDismiss,
    onConfirm = onConfirm
)

@Composable
fun DeleteAbilityDialog(
    abilityName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) = DeleteConfirmDialog(
    emoji = "🗑",
    title = "Supprimer la capacité ?",
    subtitle = "\"$abilityName\" sera supprimée définitivement.",
    onDismiss = onDismiss,
    onConfirm = onConfirm
)

@Composable
fun DeleteNoteDialog(
    noteTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) = DeleteConfirmDialog(
    emoji = "🗑",
    title = "Supprimer la note ?",
    subtitle = "\"$noteTitle\" sera supprimée définitivement.",
    onDismiss = onDismiss,
    onConfirm = onConfirm
)
