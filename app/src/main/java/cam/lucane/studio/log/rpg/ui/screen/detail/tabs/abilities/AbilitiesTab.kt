package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.abilities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import cam.lucane.studio.log.rpg.data.entity.Ability
import cam.lucane.studio.log.rpg.ui.components.common.EmptySearchState
import cam.lucane.studio.log.rpg.ui.components.common.SearchBar
import cam.lucane.studio.log.rpg.ui.dialog.abilities.AbilityDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlin.text.contains

@Composable
fun AbilitiesTab(characterId: Long, viewModel: CharacterDetailViewModel) {
    val abilities by viewModel.abilities.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredAbilities = remember(abilities, searchQuery) {
        if (searchQuery.isBlank()) abilities
        else abilities.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.category?.contains(searchQuery, ignoreCase = true) == true ||
                    it.cost?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(16.dp),
                containerColor = AccentPurple
            ) {
                Icon(Icons.Default.Add, "Ajouter", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barre de recherche
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Rechercher une capacité...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )

            if (filteredAbilities.isEmpty()) {
                EmptySearchState(
                    message = if (searchQuery.isBlank()) "Aucune capacité" else "Aucun résultat pour \"$searchQuery\"",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, bottom = 100.dp, top = 4.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAbilities, key = { it.id }) { ability ->
                        AbilityCard(ability, viewModel)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AbilityDialog(
            title = "Nouvelle capacité",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, cost, range, duration, category ->
                viewModel.addAbility(
                    Ability(
                        characterId = characterId,
                        name = name,
                        description = desc,
                        cost = cost.ifBlank { null },
                        range = range.ifBlank { null },
                        duration = duration.ifBlank { null },
                        category = category.ifBlank { null }
                    )
                )
                showAddDialog = false
            }
        )
    }
}