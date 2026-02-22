package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.ui.components.common.EmptySearchState
import cam.lucane.studio.log.rpg.ui.components.common.FilterChip
import cam.lucane.studio.log.rpg.ui.components.common.SearchBar
import cam.lucane.studio.log.rpg.ui.components.common.buttons.DotButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.FloatingDotButton
import cam.lucane.studio.log.rpg.ui.dialog.inventory.ItemDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.utils.getAccentColorByCharacterId
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlin.text.contains

enum class ItemFilter { ALL, CONSUMABLE, EQUIPPED }

@Composable
fun InventoryTab(characterId: Long, viewModel: CharacterDetailViewModel) {
    val items by viewModel.items.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf(ItemFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

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

    // Détecte si le DotButton (dernier item) est visible même partiellement
    val isDotButtonVisible by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible?.index == totalItems - 1
        }
    }

    val mainColor = getAccentColorByCharacterId(characterId)

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isDotButtonVisible,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                FloatingDotButton(
                    onClick = { showAddDialog = true },
                    dashColor = mainColor.copy(0.4f),
                    labelColor = mainColor,
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Barre de recherche + filtres
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Rechercher un objet...",
                    mainColor = mainColor
                )
                // Filtres chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            label = "Tous (${items.size})",
                            selected = currentFilter == ItemFilter.ALL,
                            color = mainColor,
                            onClick = { currentFilter = ItemFilter.ALL }
                        )
                    }
                    item {
                        FilterChip(
                            label = "🧪 Consomm. (${items.count { it.isConsumable }})",
                            selected = currentFilter == ItemFilter.CONSUMABLE,
                            color = ColorsSystem.Red,
                            onClick = { currentFilter = ItemFilter.CONSUMABLE }
                        )
                    }
                    item {
                        FilterChip(
                            label = "✓ Équipés (${items.count { it.isEquipped }})",
                            selected = currentFilter == ItemFilter.EQUIPPED,
                            color = ColorsSystem.Green,
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
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        ItemCard(mainColor, item, viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(1.dp)) }
                    item {
                        DotButton(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            label = "＋ Ajouter un objet",
                            dashColor = mainColor.copy(0.4f),
                            labelColor = mainColor,
                            onClick = { showAddDialog = true }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(1.dp)) }
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