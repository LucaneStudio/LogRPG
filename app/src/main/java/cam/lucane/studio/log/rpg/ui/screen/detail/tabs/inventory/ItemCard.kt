package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Item
import cam.lucane.studio.log.rpg.ui.dialog.inventory.ItemDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.GlassSurface
import cam.lucane.studio.log.rpg.ui.theme.HealthRed
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

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