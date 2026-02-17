package cam.lucane.studio.log.rpg.ui.screen.tabs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.CurrencyMode
import cam.lucane.studio.log.rpg.data.entity.getCurrencyDisplay
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

// ── Onglet Compteurs (point d'entrée) ───────────────────────────────────────

@Composable
fun CountersTab(character: Character, viewModel: CharacterDetailViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { HealthCard(character, viewModel) }
        item { ManaCard(character, viewModel) }
        item { CurrencyCard(character, viewModel) }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ── Cartes individuelles ────────────────────────────────────────────────────

@Composable
fun HealthCard(character: Character, viewModel: CharacterDetailViewModel) {
    var showEditMaxDialog by remember { mutableStateOf(false) }

    StatCounterCard(
        label = "POINTS DE VIE",
        current = character.currentHealth,
        max = character.maxHealth,
        accentColor = HealthRed,
        temporaryLabel = if (character.currentHealth > character.maxHealth)
            "PV temporaires: +${character.currentHealth - character.maxHealth}" else null,
        onMinus = {
            if (character.currentHealth > 0)
                viewModel.updateHealth(character.currentHealth - 1, character.maxHealth)
        },
        onPlus = {
            viewModel.updateHealth(character.currentHealth + 1, character.maxHealth)
        },
        onReset = {
            viewModel.updateHealth(character.maxHealth, character.maxHealth)
        },
        onEditMax = { showEditMaxDialog = true }
    )

    if (showEditMaxDialog) {
        EditMaxDialog(
            title = "PV Maximum",
            currentValue = character.maxHealth,
            onDismiss = { showEditMaxDialog = false },
            onConfirm = { newMax ->
                viewModel.updateHealth(character.currentHealth, newMax)
                showEditMaxDialog = false
            }
        )
    }
}

@Composable
fun ManaCard(character: Character, viewModel: CharacterDetailViewModel) {
    var showEditMaxDialog by remember { mutableStateOf(false) }

    StatCounterCard(
        label = "MANA",
        current = character.currentMana,
        max = character.maxMana,
        accentColor = ManaBlue,
        temporaryLabel = if (character.currentMana > character.maxMana)
            "Mana temporaire: +${character.currentMana - character.maxMana}" else null,
        onMinus = {
            if (character.currentMana > 0)
                viewModel.updateMana(character.currentMana - 1, character.maxMana)
        },
        onPlus = {
            viewModel.updateMana(character.currentMana + 1, character.maxMana)
        },
        onReset = {
            viewModel.updateMana(character.maxMana, character.maxMana)
        },
        onEditMax = { showEditMaxDialog = true }
    )

    if (showEditMaxDialog) {
        EditMaxDialog(
            title = "Mana Maximum",
            currentValue = character.maxMana,
            onDismiss = { showEditMaxDialog = false },
            onConfirm = { newMax ->
                viewModel.updateMana(character.currentMana, newMax)
                showEditMaxDialog = false
            }
        )
    }
}

@Composable
private fun StatCounterCard(
    label: String,
    current: Int,
    max: Int,
    accentColor: Color,
    temporaryLabel: String?,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onReset: () -> Unit,
    onEditMax: () -> Unit
) {
    val progress = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f
    val isAtMax = current == max

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {

            Column(modifier = Modifier.padding(16.dp)) {
                // Ligne titre + boutons utilitaires
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.5.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Reset
                        SmallIconBtn(
                            onClick = onReset,
                            enabled = !isAtMax,
                            tint = if (!isAtMax) AccentGreen else TextSecondary.copy(alpha = 0.3f)
                        ) {
                            Icon(Icons.Default.Refresh, "Réinitialiser", modifier = Modifier.size(16.dp))
                        }
                        // Edit max
                        SmallIconBtn(onClick = onEditMax) {
                            Icon(Icons.Default.Edit, "Modifier max", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Valeur + contrôles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Bouton -
                    ControlButton(
                        onClick = onMinus,
                        color = accentColor.copy(alpha = 0.15f),
                        borderColor = accentColor.copy(alpha = 0.3f)
                    ) {
                        Icon(Icons.Default.Remove, "Moins", tint = accentColor, modifier = Modifier.size(18.dp))
                    }

                    // Valeur
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$current",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        Text(
                            text = "/ $max",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Bouton +
                    ControlButton(
                        onClick = onPlus,
                        color = accentColor.copy(alpha = 0.15f),
                        borderColor = accentColor.copy(alpha = 0.3f)
                    ) {
                        Icon(Icons.Default.Add, "Plus", tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barre de progression
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(accentColor, accentColor.copy(alpha = 0.6f))
                                )
                            )
                    )
                }

                // Label PV temporaires
                temporaryLabel?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlButton(
    onClick: () -> Unit,
    color: Color,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SmallIconBtn(
    onClick: () -> Unit,
    enabled: Boolean = true,
    tint: Color = TextSecondary,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(28.dp)
    ) {
        content()
    }
}

// ── Monnaie ──────────────────────────────────────────────────────────────────

@Composable
fun CurrencyCard(character: Character, viewModel: CharacterDetailViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showSpendDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    val display = character.getCurrencyDisplay()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "MONNAIE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.5.sp
                    )
                    SmallIconBtn(onClick = { showModeDialog = true }) {
                        Icon(Icons.Default.Settings, "Mode", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Affichage selon le mode
                when (character.currencyMode) {
                    CurrencyMode.SINGLE -> {
                        Text(
                            "${character.credits}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                        Text("crédits", fontSize = 13.sp, color = TextSecondary)
                    }
                    CurrencyMode.BY_TEN, CurrencyMode.BY_HUNDRED -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            CoinDisplay("Or", display.gold, AccentGold)
                            CoinDisplay("Argent", display.silver, TextSecondary)
                            CoinDisplay("Cuivre", display.copper, Color(0xFFCD7F32))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Total : ${character.credits} crédits",
                            fontSize = 11.sp,
                            color = TextSecondary.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, AccentGold.copy(alpha = 0.4f)
                        )
                    ) {
                        Icon(Icons.Default.Add, null, tint = AccentGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ajouter", color = AccentGold, fontSize = 13.sp)
                    }
                    OutlinedButton(
                        onClick = { showSpendDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, BorderSubtle
                        )
                    ) {
                        Icon(Icons.Default.Remove, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dépenser", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CurrencyInputDialog(
            title = "Ajouter de l'argent",
            mode = character.currencyMode,
            onDismiss = { showAddDialog = false },
            onConfirm = { credits ->
                viewModel.addCredits(credits)
                showAddDialog = false
            }
        )
    }

    if (showSpendDialog) {
        CurrencyInputDialog(
            title = "Dépenser de l'argent",
            mode = character.currencyMode,
            onDismiss = { showSpendDialog = false },
            onConfirm = { credits ->
                viewModel.spendCredits(credits) { }
                showSpendDialog = false
            }
        )
    }

    if (showModeDialog) {
        CurrencyModeDialog(
            currentMode = character.currencyMode,
            onDismiss = { showModeDialog = false },
            onConfirm = { mode ->
                viewModel.updateCurrencyMode(mode)
                showModeDialog = false
            }
        )
    }
}

@Composable
private fun CoinDisplay(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

// ── Dialogues partagés ───────────────────────────────────────────────────────

@Composable
fun EditMaxDialog(
    title: String,
    currentValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var value by remember { mutableStateOf(currentValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Valeur maximum") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(value.toIntOrNull() ?: currentValue) },
                enabled = value.toIntOrNull() != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun CurrencyInputDialog(
    title: String,
    mode: CurrencyMode,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var gold by remember { mutableStateOf("0") }
    var silver by remember { mutableStateOf("0") }
    var copper by remember { mutableStateOf("0") }

    val totalCredits = when (mode) {
        CurrencyMode.SINGLE -> copper.toIntOrNull() ?: 0
        CurrencyMode.BY_TEN -> ((gold.toIntOrNull() ?: 0) * 100) +
                ((silver.toIntOrNull() ?: 0) * 10) +
                (copper.toIntOrNull() ?: 0)
        CurrencyMode.BY_HUNDRED -> ((gold.toIntOrNull() ?: 0) * 10000) +
                ((silver.toIntOrNull() ?: 0) * 100) +
                (copper.toIntOrNull() ?: 0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (mode) {
                    CurrencyMode.SINGLE -> {
                        OutlinedTextField(
                            value = copper, onValueChange = { copper = it },
                            label = { Text("Crédits") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    CurrencyMode.BY_TEN, CurrencyMode.BY_HUNDRED -> {
                        OutlinedTextField(
                            value = gold, onValueChange = { gold = it },
                            label = { Text("Or (Po)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = silver, onValueChange = { silver = it },
                            label = { Text("Argent (Pa)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = copper, onValueChange = { copper = it },
                            label = { Text("Cuivre (Pc)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        HorizontalDivider()
                        Text(
                            "Total : $totalCredits crédits",
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(totalCredits) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun CurrencyModeDialog(
    currentMode: CurrencyMode,
    onDismiss: () -> Unit,
    onConfirm: (CurrencyMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mode de monnaie") },
        text = {
            Column {
                CurrencyMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(mode) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = mode == currentMode, onClick = { onConfirm(mode) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                when (mode) {
                                    CurrencyMode.SINGLE -> "Monnaie unique"
                                    CurrencyMode.BY_TEN -> "Conversion ×10"
                                    CurrencyMode.BY_HUNDRED -> "Conversion ×100"
                                },
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                when (mode) {
                                    CurrencyMode.SINGLE -> "Affichage direct en crédits"
                                    CurrencyMode.BY_TEN -> "1 Po = 100c, 1 Pa = 10c, 1 Pc = 1c"
                                    CurrencyMode.BY_HUNDRED -> "1 Po = 10000c, 1 Pa = 100c"
                                },
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        }
    )
}