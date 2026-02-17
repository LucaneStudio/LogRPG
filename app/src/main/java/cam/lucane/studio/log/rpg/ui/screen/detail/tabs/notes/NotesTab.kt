package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

@Composable
fun NotesTab(
    character: cam.lucane.studio.log.rpg.data.entity.Character,
    viewModel: CharacterDetailViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Bientôt disponible…", color = TextSecondary)
    }
}