package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.abilities

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
import cam.lucane.studio.log.rpg.data.entity.Ability
import cam.lucane.studio.log.rpg.ui.components.common.EmptySearchState
import cam.lucane.studio.log.rpg.ui.components.common.SearchBar
import cam.lucane.studio.log.rpg.ui.components.common.buttons.DotButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.FloatingDotButton
import cam.lucane.studio.log.rpg.ui.dialog.abilities.AbilityDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.utils.getAccentBrushByCharacterId
import cam.lucane.studio.log.rpg.ui.utils.getAccentColorByCharacterId
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlin.text.contains

@Composable
fun AbilitiesTab(characterId: Long, viewModel: CharacterDetailViewModel) {
    val abilities by viewModel.abilities.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val filteredAbilities = remember(abilities, searchQuery) {
        if (searchQuery.isBlank()) abilities
        else abilities.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.category?.contains(searchQuery, ignoreCase = true) == true ||
                    it.cost?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    val mainColor = getAccentColorByCharacterId(characterId)
    val mainBrush = getAccentBrushByCharacterId(characterId)

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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Barre de recherche
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Rechercher une capacité...",
                modifier = Modifier
                    .fillMaxWidth(),
                mainColor = mainColor,
            )

            if (filteredAbilities.isEmpty()) {
                EmptySearchState(
                    message = if (searchQuery.isBlank()) "Aucune capacité" else "Aucun résultat pour \"$searchQuery\"",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAbilities, key = { it.id }) { ability ->
                        AbilityCard(mainColor, mainBrush, ability, viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(1.dp)) }
                    item {
                        DotButton(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            label = "＋ Ajouter une capacité",
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
            },
            mainColor = mainColor,
            mainBrush = mainBrush
        )
    }
}