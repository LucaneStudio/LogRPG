package cam.lucane.studio.log.rpg.ui.screen.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Item
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

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
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Rechercher un objet..."
                )
                // Filtres chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterPill(
                            label = "Tous (${items.size})",
                            selected = currentFilter == ItemFilter.ALL,
                            color = AccentPurple,
                            onClick = { currentFilter = ItemFilter.ALL }
                        )
                    }
                    item {
                        FilterPill(
                            label = "🧪 Consomm. (${items.count { it.isConsumable }})",
                            selected = currentFilter == ItemFilter.CONSUMABLE,
                            color = AccentRed,
                            onClick = { currentFilter = ItemFilter.CONSUMABLE }
                        )
                    }
                    item {
                        FilterPill(
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

@Composable
fun ItemCard(item: Item, viewModel: CharacterDetailViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val accentColor = when {
        item.isConsumable -> AccentRed
        item.isEquipped -> AccentGreen
        else -> AccentPurple.copy(alpha = 0.6f)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isEquipped) AccentGreen.copy(alpha = 0.2f) else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Box {
            // Barre accent gauche
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(accentColor, accentColor.copy(alpha = 0.1f))
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(
                    start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icône catégorie
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = categoryEmoji(item.category),
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 3.dp)
                        ) {
                            item.category?.let {
                                ItemBadge(it, accentColor)
                            }
                            if (item.isEquipped) {
                                ItemBadge("Équipé", AccentGreen)
                            }
                            if (item.isConsumable) {
                                ItemBadge("Consomm.", AccentRed)
                            }
                        }
                    }

                    // Partie droite : compteur OU quantité + actions
                    Column(horizontalAlignment = Alignment.End) {
                        if (item.isConsumable) {
                            // Compteur +/-
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                ConsumableBtn(
                                    onClick = {
                                        if (item.quantity > 0)
                                            viewModel.updateItem(item.copy(quantity = item.quantity - 1))
                                    },
                                    enabled = item.quantity > 0
                                ) {
                                    Icon(
                                        Icons.Default.Remove, "Utiliser",
                                        modifier = Modifier.size(14.dp),
                                        tint = if (item.quantity > 0) AccentRed else TextSecondary.copy(alpha = 0.3f)
                                    )
                                }
                                Text(
                                    text = "${item.quantity}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentRed,
                                    modifier = Modifier.widthIn(min = 20.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                ConsumableBtn(
                                    onClick = {
                                        viewModel.updateItem(item.copy(quantity = item.quantity + 1))
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Add, "Ajouter",
                                        modifier = Modifier.size(14.dp),
                                        tint = AccentRed
                                    )
                                }
                            }
                        } else {
                            // Quantité simple
                            if (item.quantity > 1) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.06f)
                                ) {
                                    Text(
                                        "×${item.quantity}",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Boutons edit/delete
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            if (!item.isConsumable) {
                                // Toggle équiper
                                IconButton(
                                    onClick = { viewModel.updateItem(item.copy(isEquipped = !item.isEquipped)) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        if (item.isEquipped) Icons.Default.CheckCircle else Icons.Default.Circle,
                                        if (item.isEquipped) "Déséquiper" else "Équiper",
                                        tint = if (item.isEquipped) AccentGreen else TextSecondary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit, "Modifier",
                                    tint = TextSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            IconButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete, "Supprimer",
                                    tint = HealthRed.copy(alpha = 0.4f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                // Description dépliable
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        HorizontalDivider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                        item.weight?.let {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("⚖️ Poids : $it", fontSize = 11.sp, color = TextSecondary.copy(alpha = 0.6f))
                        }
                        item.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("📝 $it", fontSize = 11.sp, color = AccentGold.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer ${item.name} ?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteItem(item)
                    showDeleteDialog = false
                }) { Text("Supprimer", color = HealthRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") }
            }
        )
    }

    if (showEditDialog) {
        ItemDialog(
            title = "Modifier l'objet",
            initialItem = item,
            onDismiss = { showEditDialog = false },
            onConfirm = { updated ->
                viewModel.updateItem(updated)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun ConsumableBtn(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (enabled) AccentRed.copy(alpha = 0.12f)
                else Color.White.copy(alpha = 0.03f)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun ItemBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.22f))
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

private fun categoryEmoji(category: String?): String {
    if (category == null) return "🎒"
    return when {
        category.contains("Arme", ignoreCase = true) -> "⚔️"
        category.contains("Armure", ignoreCase = true) -> "🛡️"
        category.contains("Potion", ignoreCase = true) ||
                category.contains("Soin", ignoreCase = true) -> "🧪"
        category.contains("Mana", ignoreCase = true) -> "💙"
        category.contains("Nourriture", ignoreCase = true) ||
                category.contains("Ration", ignoreCase = true) -> "🍖"
        category.contains("Parchemin", ignoreCase = true) ||
                category.contains("Livre", ignoreCase = true) ||
                category.contains("Grimoire", ignoreCase = true) -> "📖"
        category.contains("Outil", ignoreCase = true) -> "🔧"
        category.contains("Magique", ignoreCase = true) -> "✨"
        category.contains("Bouclier", ignoreCase = true) -> "🛡️"
        category.contains("Lumière", ignoreCase = true) -> "🔦"
        else -> "🎒"
    }
}

// ── Dialogue objet (ajout + édition) ────────────────────────────────────────

@Composable
fun ItemDialog(
    title: String,
    initialItem: Item? = null,
    onDismiss: () -> Unit,
    onConfirm: (Item) -> Unit
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var quantity by remember { mutableStateOf((initialItem?.quantity ?: 1).toString()) }
    var weight by remember { mutableStateOf(initialItem?.weight ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "") }
    var notes by remember { mutableStateOf(initialItem?.notes ?: "") }
    var isConsumable by remember { mutableStateOf(initialItem?.isConsumable ?: false) }
    var isEquipped by remember { mutableStateOf(initialItem?.isEquipped ?: false) }

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
                    value = description, onValueChange = { description = it },
                    label = { Text("Description *") },
                    minLines = 2, maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Quantité") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight, onValueChange = { weight = it },
                        label = { Text("Poids") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = category, onValueChange = { category = it },
                    label = { Text("Catégorie") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(color = BorderSubtle)
                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Consommable", fontSize = 14.sp, color = TextPrimary)
                    Switch(
                        checked = isConsumable,
                        onCheckedChange = { isConsumable = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentRed, checkedTrackColor = AccentRed.copy(alpha = 0.3f))
                    )
                }
                if (!isConsumable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Équipé", fontSize = 14.sp, color = TextPrimary)
                        Switch(
                            checked = isEquipped,
                            onCheckedChange = { isEquipped = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = AccentGreen.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        (initialItem ?: Item(characterId = 0, name = "", description = "")).copy(
                            name = name,
                            description = description,
                            quantity = quantity.toIntOrNull() ?: 1,
                            weight = weight.ifBlank { null },
                            category = category.ifBlank { null },
                            notes = notes.ifBlank { null },
                            isConsumable = isConsumable,
                            isEquipped = if (isConsumable) false else isEquipped
                        )
                    )
                },
                enabled = name.isNotBlank() && description.isNotBlank()
            ) {
                Text(if (initialItem == null) "Ajouter" else "Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

// ── Composants partagés ──────────────────────────────────────────────────────

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(placeholder, color = TextSecondary) },
        leadingIcon = {
            Icon(Icons.Default.Search, "Rechercher", tint = TextSecondary, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Effacer", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentPurple.copy(alpha = 0.6f),
            unfocusedBorderColor = BorderSubtle,
            focusedContainerColor = GlassSurface,
            unfocusedContainerColor = GlassSurface,
            cursorColor = AccentPurple
        )
    )
}

@Composable
fun FilterPill(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) color.copy(alpha = 0.15f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) color.copy(alpha = 0.4f) else BorderSubtle
        )
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) color else TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun EmptySearchState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔍", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, fontSize = 15.sp, color = TextSecondary)
    }
}