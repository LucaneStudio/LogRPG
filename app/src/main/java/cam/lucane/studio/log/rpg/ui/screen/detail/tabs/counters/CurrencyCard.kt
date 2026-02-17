package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.CurrencyMode
import cam.lucane.studio.log.rpg.data.entity.getCurrencyDisplay
import cam.lucane.studio.log.rpg.ui.components.common.SmallIconBtn
import cam.lucane.studio.log.rpg.ui.dialog.counters.CurrencyInputDialog
import cam.lucane.studio.log.rpg.ui.dialog.counters.CurrencyModeDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentCopper
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.GlassSurface
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

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
                            CoinDisplay("Cuivre", display.copper,AccentCopper)
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