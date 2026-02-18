package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.NotesMode
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun NotesHeader(
    currentMode: NotesMode,
    isSaved: Boolean,
    onModeChange: (NotesMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Titre
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Notes,
                contentDescription = null,
                tint = AccentPurple,
                modifier = Modifier.size(20.dp)
            )
            Text(
                "NOTES LIBRES",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
        }

        // Toggle mode + indicateur sauvegarde
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle Edit/Render
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GlassSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row {
                    // Bouton Edit
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                            .background(
                                if (currentMode == NotesMode.EDIT)
                                    AccentPurple.copy(alpha = 0.15f)
                                else
                                    androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable { onModeChange(NotesMode.EDIT) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = "Mode édition",
                                tint = if (currentMode == NotesMode.EDIT) AccentPurple else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Edit",
                                fontSize = 11.sp,
                                fontWeight = if (currentMode == NotesMode.EDIT) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (currentMode == NotesMode.EDIT) AccentPurple else TextSecondary
                            )
                        }
                    }

                    // Séparateur
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(BorderSubtle)
                    )

                    // Bouton Render
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                            .background(
                                if (currentMode == NotesMode.RENDER)
                                    AccentPurple.copy(alpha = 0.15f)
                                else
                                    androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable { onModeChange(NotesMode.RENDER) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Preview,
                                contentDescription = "Mode aperçu",
                                tint = if (currentMode == NotesMode.RENDER) AccentPurple else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Preview",
                                fontSize = 11.sp,
                                fontWeight = if (currentMode == NotesMode.RENDER) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (currentMode == NotesMode.RENDER) AccentPurple else TextSecondary
                            )
                        }
                    }
                }
            }

            // Indicateur de sauvegarde
            Text(
                if (isSaved) "✓" else "●",
                fontSize = 16.sp,
                color = if (isSaved) AccentGreen else AccentGold
            )
        }
    }
}