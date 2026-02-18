package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components.*
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlinx.coroutines.delay

enum class NotesMode {
    EDIT,    // Mode édition (syntaxe markdown visible)
    RENDER   // Mode rendu (markdown formaté)
}

@Composable
fun NotesTab(
    character: Character,
    viewModel: CharacterDetailViewModel
) {
    var notes by remember { mutableStateOf(character.notes ?: "") }
    var lastSavedNotes by remember { mutableStateOf(character.notes ?: "") }
    var mode by remember { mutableStateOf(NotesMode.EDIT) }

    // Auto-save avec debounce de 1 seconde
    LaunchedEffect(notes) {
        if (notes != lastSavedNotes) {
            delay(1000)
            viewModel.updateNotes(notes)
            lastSavedNotes = notes
        }
    }

    // Synchroniser avec les changements externes
    LaunchedEffect(character.notes) {
        if (character.notes != notes) {
            notes = character.notes ?: ""
            lastSavedNotes = character.notes ?: ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        AccentPurple.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(300f, 200f),
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
            // En-tête avec toggle mode + indicateur sauvegarde
            NotesHeader(
                currentMode = mode,
                isSaved = notes == lastSavedNotes,
                onModeChange = { mode = it }
            )

            // Zone d'édition/rendu selon le mode
            Box(modifier = Modifier.weight(1f)) {
                when (mode) {
                    NotesMode.EDIT -> NotesEditor(
                        notes = notes,
                        onNotesChange = { notes = it }
                    )
                    NotesMode.RENDER -> NotesRenderer(
                        markdown = notes
                    )
                }
            }

            // Stats en pied de page
            NotesStats(
                characterCount = notes.length,
                lineCount = notes.lines().size
            )
        }
    }
}