package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import cam.lucane.studio.log.rpg.ui.components.common.EmptySearchState
import cam.lucane.studio.log.rpg.ui.components.common.EmptyState
import cam.lucane.studio.log.rpg.ui.components.common.SearchBar
import cam.lucane.studio.log.rpg.ui.components.common.buttons.DotButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.FloatingDotButton
import cam.lucane.studio.log.rpg.ui.dialog.notes.NoteDialog
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components.NoteCard
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

enum class NotesMode { EDIT, RENDER }

@Composable
fun NotesTab(
    mainColor: Color,
    notes: List<Note>,
    viewModel: CharacterDetailViewModel
) {
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var noteToRename by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val filteredNotes = remember(notes, searchQuery) {
        if (searchQuery.isBlank()) notes
        else notes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true)
        }
    }
    // Garder la note éditée à jour si elle change en base
    val updatedNote = editingNote?.let { editing -> notes.find { it.id == editing.id } }

    // Détecte si le DotButton (dernier item) est visible même partiellement
    val isDotButtonVisible by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible?.index == totalItems - 1
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isDotButtonVisible,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                FloatingDotButton(
                    onClick = { showCreateDialog = true },
                    dashColor = mainColor.copy(0.4f),
                    labelColor = mainColor,
                )
            }
        }
    ) { padding ->
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
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "Rechercher une note...",
                            mainColor = mainColor,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (filteredNotes.isEmpty()) {
                            EmptySearchState(
                                message = if (searchQuery.isBlank()) "Aucune Notes" else "Aucun résultat pour \"$searchQuery\"",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {

                            LazyColumn(
                                state = listState,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(filteredNotes, key = { it.id }) { note ->
                                    NoteCard(
                                        mainColor = mainColor,
                                        note = note,
                                        onClick = { editingNote = note },
                                        onRename = { noteToRename = note },
                                        onDelete = { noteToDelete = note }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(1.dp)) }
                                item {
                                    DotButton(
                                        modifier = Modifier.fillMaxWidth(0.9f),
                                        label = "＋ Créer une note",
                                        dashColor = mainColor.copy(0.4f),
                                        labelColor = mainColor,
                                        onClick = { showCreateDialog = true }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(1.dp)) }
                            }
                        }
                    }
                }
            } else {
                // ── Vue éditeur ────────────────────────────────────────
                NoteEditorScreen(
                    note = updatedNote ?: noteBeingEdited,
                    viewModel = viewModel,
                    onBack = { editingNote = null },
                    mainColor = mainColor
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