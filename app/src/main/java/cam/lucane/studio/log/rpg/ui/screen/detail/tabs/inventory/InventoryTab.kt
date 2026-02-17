package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.ui.components.common.EmptySearchState
import cam.lucane.studio.log.rpg.ui.components.common.FilterChip
import cam.lucane.studio.log.rpg.ui.components.common.SearchBar
import cam.lucane.studio.log.rpg.ui.dialog.inventory.ItemDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlin.text.contains

enum class ItemFilter { ALL, CONSUMABLE, EQUIPPED }

@Composable
fun InventoryTab(characterId: Long, viewModel: CharacterDetailViewModel) {
    val items by viewModel.items.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf(ItemFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(items, currentFilter, searchQuery) {
        var result = when (currentFilter) {
            ItemFilter.ALL -> items
            ItemFilter.CONSUMABLE -> items.filter { it.isConsumable }
            ItemFilter.EQUIPPED -> items.filter { it.isEquipped }
        }
        if (searchQuery.isNotBlank()) {
            result = result.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true) ||
                        it.category?.contains(searchQuery, ignoreCase = true) == true ||
                        it.notes?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        result
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(16.dp),
                containerColor = AccentPurple,
            ) {
                Icon(Icons.Default.Add, "Ajouter")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barre de recherche + filtres
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 4.dp),
            ) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Rechercher un objet..."
                )
                // Filtres chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            label = "Tous (${items.size})",
                            selected = currentFilter == ItemFilter.ALL,
                            color = AccentPurple,
                            onClick = { currentFilter = ItemFilter.ALL }
                        )
                    }
                    item {
                        FilterChip(
                            label = "🧪 Consomm. (${items.count { it.isConsumable }})",
                            selected = currentFilter == ItemFilter.CONSUMABLE,
                            color = AccentRed,
                            onClick = { currentFilter = ItemFilter.CONSUMABLE }
                        )
                    }
                    item {
                        FilterChip(
                            label = "✓ Équipés (${items.count { it.isEquipped }})",
                            selected = currentFilter == ItemFilter.EQUIPPED,
                            color = AccentGreen,
                            onClick = { currentFilter = ItemFilter.EQUIPPED }
                        )
                    }
                }
            }

            if (filteredItems.isEmpty()) {
                EmptySearchState(
                    message = if (searchQuery.isBlank()) "Aucun objet" else "Aucun résultat pour \"$searchQuery\"",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, bottom = 100.dp, top = 4.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        ItemCard(item, viewModel)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ItemDialog(
            title = "Nouvel objet",
            onDismiss = { showAddDialog = false },
            onConfirm = { item ->
                viewModel.addItem(item.copy(characterId = characterId))
                showAddDialog = false
            }
        )
    }
}