package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
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
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlinx.coroutines.delay
import java.nio.file.WatchEvent

@Composable
fun NoteEditorScreen(
    note: Note,
    mainColor: Color,
    viewModel: CharacterDetailViewModel,
    onBack: () -> Unit
) {
    var content by remember(note.id) { mutableStateOf(note.content) }
    var lastSaved by remember(note.id) { mutableStateOf(note.content) }

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
            .clip(RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp)
            .background(ColorsSystem.BackgroundCard, shape = RoundedCornerShape(18.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Barre du haut ──────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Icon(
                    Icons.Default.ArrowBack,
                    "Précédent",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable{onBack.invoke()},
                    tint = ColorsSystem.TextSecondary
                )

                Text(
                    text = "\uD83D\uDCDD " + note.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorsSystem.TextPrimary,
                    fontFamily = NunitoFontFamily
                )
            }


            // Indicateur sauvegarde
            Text(
                text = if (content == lastSaved) "✓" else "●",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (content == lastSaved) mainColor else ColorsSystem.Cyan,
                fontFamily = NunitoFontFamily

            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = ColorsSystem.Divider,
            thickness = 1.dp
        )

        // ── Zone d'édition ─────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f).background(Color.Red)) {
            NotesEditor(mainColor = mainColor, notes = content, onNotesChange = { content = it })
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = ColorsSystem.Divider,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "${content.length} caractères",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily
            )

            Text(
                text = "${content.lines().size} lignes",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily
            )
        }
    }
}