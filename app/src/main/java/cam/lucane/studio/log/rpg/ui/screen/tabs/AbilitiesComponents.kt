package cam.lucane.studio.log.rpg.ui.screen.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Ability
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

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

@Composable
fun AbilityCard(ability: Ability, viewModel: CharacterDetailViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Couleur par catégorie
    val categoryColor = remember(ability.category) {
        when {
            ability.category?.contains("Combat", ignoreCase = true) == true -> AccentRed
            ability.category?.contains("Magie", ignoreCase = true) == true -> ManaBlue
            ability.category?.contains("Social", ignoreCase = true) == true -> AccentGold
            ability.category?.contains("Passive", ignoreCase = true) == true -> AccentGreen
            else -> AccentPurple
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Box {
            // Barre accent gauche par catégorie
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(categoryColor, categoryColor.copy(alpha = 0.2f))
                        )
                    )
            )

            Column(modifier = Modifier.padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)) {
                // Ligne principale
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ability.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        // Badges catégorie + coût
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            ability.category?.let {
                                AbilityBadge(text = it, color = categoryColor)
                            }
                            ability.cost?.let {
                                AbilityBadge(text = it, color = AccentGold)
                            }
                        }
                    }

                    // Boutons action
                    Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                "Modifier",
                                tint = TextSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                "Supprimer",
                                tint = HealthRed.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            "Détail",
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

                // Détail dépliable
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HorizontalDivider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = ability.description,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 19.sp
                        )
                        // Portée + durée
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ability.range?.let {
                                AbilityDetailRow(icon = "🎯", label = "Portée", value = it)
                            }
                            ability.duration?.let {
                                AbilityDetailRow(icon = "⏱️", label = "Durée", value = it)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer ${ability.name} ?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAbility(ability)
                    showDeleteDialog = false
                }) { Text("Supprimer", color = HealthRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") }
            }
        )
    }

    if (showEditDialog) {
        AbilityDialog(
            title = "Modifier la capacité",
            initialName = ability.name,
            initialDesc = ability.description,
            initialCost = ability.cost ?: "",
            initialRange = ability.range ?: "",
            initialDuration = ability.duration ?: "",
            initialCategory = ability.category ?: "",
            onDismiss = { showEditDialog = false },
            onConfirm = { name, desc, cost, range, duration, category ->
                viewModel.updateAbility(
                    ability.copy(
                        name = name,
                        description = desc,
                        cost = cost.ifBlank { null },
                        range = range.ifBlank { null },
                        duration = duration.ifBlank { null },
                        category = category.ifBlank { null }
                    )
                )
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun AbilityBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AbilityDetailRow(icon: String, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 12.sp)
        Text(
            "$label : ",
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.6f)
        )
        Text(
            value,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Dialogue capacité (ajout + édition) ──────────────────────────────────────

@Composable
fun AbilityDialog(
    title: String,
    initialName: String = "",
    initialDesc: String = "",
    initialCost: String = "",
    initialRange: String = "",
    initialDuration: String = "",
    initialCategory: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc) }
    var cost by remember { mutableStateOf(initialCost) }
    var range by remember { mutableStateOf(initialRange) }
    var duration by remember { mutableStateOf(initialDuration) }
    var category by remember { mutableStateOf(initialCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nom *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc, onValueChange = { desc = it },
                    label = { Text("Description *") },
                    minLines = 2, maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cost, onValueChange = { cost = it },
                        label = { Text("Coût") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = range, onValueChange = { range = it },
                        label = { Text("Portée") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = duration, onValueChange = { duration = it },
                        label = { Text("Durée") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category, onValueChange = { category = it },
                        label = { Text("Catégorie") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, desc, cost, range, duration, category) },
                enabled = name.isNotBlank() && desc.isNotBlank()
            ) {
                Text(if (initialName.isEmpty()) "Ajouter" else "Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}