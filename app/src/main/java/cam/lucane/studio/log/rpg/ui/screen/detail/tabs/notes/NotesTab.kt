package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Note
import cam.lucane.studio.log.rpg.ui.components.common.EmptyState
import cam.lucane.studio.log.rpg.ui.dialog.notes.NoteDialog
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components.NoteCard
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

enum class NotesMode { EDIT, RENDER }

@Composable
fun NotesTab(
    notes: List<Note>,
    viewModel: CharacterDetailViewModel
) {
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var noteToRename by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    // Garder la note éditée à jour si elle change en base
    val updatedNote = editingNote?.let { editing -> notes.find { it.id == editing.id } }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = editingNote,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "notes_transition"
        ) { noteBeingEdited ->

            if (noteBeingEdited == null) {
                // ── Vue liste ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AccentPurple.copy(alpha = 0.03f), Color.Transparent),
                                radius = 800f
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // En-tête
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                    "NOTES (${notes.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp,
                                    color = TextSecondary
                                )
                            }
                            Button(
                                onClick = { showCreateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Nouvelle note", fontSize = 13.sp)
                            }
                        }

                        if (notes.isEmpty()) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                EmptyState(
                                    emoji = "📝",
                                    message = "Aucune note pour l'instant\nAppuie sur \"Nouvelle note\" pour commencer"
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(notes, key = { it.id }) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = { editingNote = note },
                                        onRename = { noteToRename = note },
                                        onDelete = { noteToDelete = note }
                                    )
                                }
                            }
                        }
                    }

                    // FAB
                    FloatingActionButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = AccentPurple,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nouvelle note")
                    }
                }

            } else {
                // ── Vue éditeur ────────────────────────────────────────
                NoteEditorScreen(
                    note = updatedNote ?: noteBeingEdited,
                    viewModel = viewModel,
                    onBack = { editingNote = null }
                )
            }
        }
    }

    // ── Dialogue création ──────────────────────────────────────────────
    if (showCreateDialog) {
        NoteDialog(
            onConfirm = { title ->
                viewModel.addNote(title) { newId ->
                    editingNote = Note(id = newId, characterId = 0L, title = title)
                }
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    // ── Dialogue renommer ──────────────────────────────────────────────
    noteToRename?.let { note ->
        NoteDialog(
            note = note,
            onConfirm = { newTitle ->
                viewModel.updateNote(note.copy(title = newTitle))
                noteToRename = null
            },
            onDismiss = { noteToRename = null }
        )
    }

    // ── Dialogue suppression ───────────────────────────────────────────
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Supprimer la note ?", color = TextPrimary) },
            text = { Text("\"${note.title}\" sera supprimée définitivement.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(note)
                    noteToDelete = null
                }) {
                    Text("Supprimer", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}