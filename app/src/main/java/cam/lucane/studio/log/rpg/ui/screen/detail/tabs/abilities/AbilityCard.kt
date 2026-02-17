package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.abilities

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import cam.lucane.studio.log.rpg.data.entity.Ability
import cam.lucane.studio.log.rpg.ui.dialog.abilities.AbilityDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.GlassSurface
import cam.lucane.studio.log.rpg.ui.theme.HealthRed
import cam.lucane.studio.log.rpg.ui.theme.ManaBlue
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import kotlin.text.contains

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