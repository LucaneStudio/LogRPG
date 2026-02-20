package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

@Composable
fun CountersTab(character: Character, viewModel: CharacterDetailViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { HealthCard(character, viewModel) }
        item { ManaCard(character, viewModel) }
        item { CurrencyCard(character, viewModel) }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}