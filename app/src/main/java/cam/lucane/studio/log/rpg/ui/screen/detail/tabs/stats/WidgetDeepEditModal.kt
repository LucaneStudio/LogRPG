package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cam.lucane.studio.log.rpg.data.entity.StatWidget
import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

/**
 * Modal ouvert par un long-press sur un widget en mode édition.
 * Permet d'éditer le titre, la couleur d'accent et de supprimer le widget.
 */
@Composable
fun WidgetDeepEditModal(
    widget: StatWidget,
    onDismiss: () -> Unit,
    onConfirm: (title: String, accentColor: WidgetAccentColor) -> Unit,
    onDelete: () -> Unit,
) {
    var title  by remember { mutableStateOf(widget.title) }
    var color  by remember { mutableStateOf(widget.widgetAccentColor()) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(ColorsSystem.BackgroundCard)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Modifier le widget",
                fontSize   = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = ColorsSystem.TextPrimary,
            )

            // Titre
            CombatTextField(
                value         = title,
                onValueChange = { title = it },
                label         = "Titre",
                modifier      = Modifier.fillMaxWidth(),
            )

            // Couleur d'accent
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "COULEUR D'ACCENT",
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    color         = ColorsSystem.TextDisabled,
                    letterSpacing = 0.8.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    WidgetAccentColor.entries.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(c.main)
                                .then(
                                    if (c == color)
                                        Modifier.border(3.dp, ColorsSystem.TextPrimary, CircleShape)
                                    else Modifier
                                )
                                .clickable { color = c }
                        )
                    }
                }
            }

            // Confirmer
            Button(
                onClick  = { onConfirm(title, color) },
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
                    "🗑 Supprimer le widget",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = ColorsSystem.Red,
                )
            }
        }
    }
}