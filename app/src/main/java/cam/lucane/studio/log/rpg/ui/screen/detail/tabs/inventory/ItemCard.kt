package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Item
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.dialog.inventory.ItemDialog
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

@Composable
fun ItemCard(mainColor: Color, item: Item, viewModel: CharacterDetailViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val accentColor = when {
        item.isConsumable -> ColorsSystem.Red
        item.isEquipped -> ColorsSystem.Green
        else -> ColorsSystem.Purple.copy(alpha = 0.6f)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(contentColor = mainColor, containerColor = ColorsSystem.BackgroundCard),
        border = BorderStroke(
            1.dp,
            if (item.isEquipped) ColorsSystem.Green.copy(alpha = 0.4f) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable { expanded = !expanded }
    ) {
        Box {
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)){
                            Text(
                                text = item.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NunitoFontFamily,
                                color = ColorsSystem.TextPrimary
                            )
                            // Quantité simple
                            if (item.quantity > 1) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.06f)
                                ) {
                                    Text(
                                        "×${item.quantity}",
                                        fontSize = 12.sp,
                                        color = ColorsSystem.TextSecondary,
                                        fontFamily = NunitoFontFamily,
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 3.dp)
                        ) {
                            item.category?.let {
                                ItemBadge(it, accentColor)
                            }
                            if (item.isEquipped) {
                                ItemBadge("Équipé", ColorsSystem.Green)
                            }
                            if (item.isConsumable) {
                                ItemBadge("Consomm.", ColorsSystem.Red)
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
                                    Text(
                                        text = "-",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (item.quantity > 0) ColorsSystem.Red else ColorsSystem.TextSecondary.copy(
                                            alpha = 0.3f
                                        ),
                                        fontFamily = NunitoFontFamily
                                    )
                                }
                                Text(
                                    text = "${item.quantity}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorsSystem.Red,
                                    modifier = Modifier.widthIn(min = 20.dp),
                                    fontFamily = NunitoFontFamily,
                                    textAlign = TextAlign.Center
                                )
                                ConsumableBtn(
                                    onClick = {
                                        viewModel.updateItem(item.copy(quantity = item.quantity + 1))
                                    }
                                ) {
                                    Text(
                                        text = "+",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (item.quantity > 0) ColorsSystem.Red else ColorsSystem.TextSecondary.copy(
                                            alpha = 0.3f
                                        ),
                                        fontFamily = NunitoFontFamily
                                    )
                                }
                            }
                        }

                        // Boutons edit/delete
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (!item.isConsumable) {
                                // Toggle équiper
                                CardOptionButton(
                                    onClick = { viewModel.updateItem(item.copy(isEquipped = !item.isEquipped)) },
                                    modifier = Modifier.size(26.dp),
                                    color = mainColor
                                ) {
                                    Icon(
                                        if (item.isEquipped) Icons.Default.CheckCircle else Icons.Default.Circle,
                                        if (item.isEquipped) "Déséquiper" else "Équiper",
                                        tint = if (item.isEquipped) ColorsSystem.Green else ColorsSystem.TextSecondary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            CardOptionButton(
                                modifier = Modifier.size(26.dp),
                                onClick = { showEditDialog = true },
                                color = mainColor
                            ) {
                                Text(
                                    text = "✏️",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = NunitoFontFamily
                                )
                            }

                            CardOptionButton(
                                modifier = Modifier.size(26.dp),
                                onClick = { showDeleteDialog = true },
                                color = mainColor
                            ) {
                                Text(
                                    text = "🗑️",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = NunitoFontFamily
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
                        HorizontalDivider(color = ColorsSystem.Divider)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = ColorsSystem.TextSecondary,
                            fontFamily = NunitoFontFamily,
                            lineHeight = 18.sp
                        )
                        item.weight?.let {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("⚖️ Poids : $it", fontSize = 11.sp, color = ColorsSystem.TextSecondary.copy(alpha = 0.6f))
                        }
                        item.notes?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("📝 $it", fontSize = 11.sp, color = ColorsSystem.Yellow.copy(alpha = 0.8f))
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
                }) { Text("Supprimer", color = ColorsSystem.Red) }
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
                if (enabled) ColorsSystem.RedLight
                else Color.White.copy(alpha = 0.03f)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun ItemBadge(text: String, color: Color) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.1f),
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp),
            fontWeight = FontWeight.Medium,
            fontFamily = NunitoFontFamily
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