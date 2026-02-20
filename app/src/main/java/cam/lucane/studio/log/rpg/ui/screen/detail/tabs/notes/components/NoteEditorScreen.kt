package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Note
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components.NotesEditor
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components.NotesRenderer
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlinx.coroutines.delay

@Composable
fun NoteEditorScreen(
    note: Note,
    viewModel: CharacterDetailViewModel,
    onBack: () -> Unit
) {
    var content by remember(note.id) { mutableStateOf(note.content) }
    var lastSaved by remember(note.id) { mutableStateOf(note.content) }
    var mode by remember { mutableStateOf(NotesMode.EDIT) }

    // Auto-save avec debounce 1s
    LaunchedEffect(content) {
        if (content != lastSaved) {
            delay(1000)
            viewModel.updateNote(note.copy(content = content))
            lastSaved = content
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Barre du haut ──────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {
                    if (content != lastSaved) viewModel.updateNote(note.copy(content = content))
                    onBack()
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextPrimary
                    )
                }
                Column {
                    Text(
                        text = note.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${content.length} car. · ${content.lines().size} lignes",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Toggle Edit / Preview
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GlassSurface,
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row {
                        // Bouton Edit
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                .background(
                                    if (mode == NotesMode.EDIT) AccentPurple.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable { mode = NotesMode.EDIT }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Code,
                                    contentDescription = "Édition",
                                    tint = if (mode == NotesMode.EDIT) AccentPurple else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "Edit",
                                    fontSize = 11.sp,
                                    fontWeight = if (mode == NotesMode.EDIT) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (mode == NotesMode.EDIT) AccentPurple else TextSecondary
                                )
                            }
                        }

                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(BorderSubtle))

                        // Bouton Preview
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                .background(
                                    if (mode == NotesMode.RENDER) AccentPurple.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable { mode = NotesMode.RENDER }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Preview,
                                    contentDescription = "Aperçu",
                                    tint = if (mode == NotesMode.RENDER) AccentPurple else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "Preview",
                                    fontSize = 11.sp,
                                    fontWeight = if (mode == NotesMode.RENDER) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (mode == NotesMode.RENDER) AccentPurple else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Indicateur sauvegarde
                Text(
                    text = if (content == lastSaved) "✓" else "●",
                    fontSize = 16.sp,
                    color = if (content == lastSaved) AccentGreen else AccentGold
                )
            }
        }

        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)

        // ── Zone d'édition ─────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            when (mode) {
                NotesMode.EDIT -> NotesEditor(notes = content, onNotesChange = { content = it })
                NotesMode.RENDER -> NotesRenderer(markdown = content)
            }
        }
    }
}