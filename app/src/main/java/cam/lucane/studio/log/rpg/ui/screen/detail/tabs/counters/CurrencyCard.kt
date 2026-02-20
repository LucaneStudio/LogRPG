package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.PrimaryButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.SmallIconBtn
import cam.lucane.studio.log.rpg.ui.dialog.counters.CurrencyInputDialog
import cam.lucane.studio.log.rpg.ui.dialog.counters.CurrencyModeDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentCopper
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.GlassSurface
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.utils.getAccentColorByCharacterId
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

@Composable
fun CurrencyCard(character: Character, viewModel: CharacterDetailViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showSpendDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }

    val display = character.getCurrencyDisplay()

    val mainColor = getAccentColorByCharacterId(character.id)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColorsSystem.BackgroundCard),
        modifier = Modifier
            .fillMaxWidth()
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            )
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "\uD83D\uDCB0 MONNAIE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.5.sp,
                        fontFamily = NunitoFontFamily
                    )
                    CardOptionButton(
                        modifier = Modifier.size(26.dp),
                        onClick = { showModeDialog = true },
                        color = mainColor
                    ) {
                        Text(
                            text = "⚙️",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = NunitoFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Affichage selon le mode
                when (character.currencyMode) {
                    CurrencyMode.SINGLE -> {
                        Text(
                            text = "${character.credits}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = NunitoFontFamily,
                            color = ColorsSystem.TextSecondary
                        )
                    }
                    CurrencyMode.BY_TEN, CurrencyMode.BY_HUNDRED -> {
                        Row(modifier = Modifier.fillMaxWidth(0.8f), horizontalArrangement = Arrangement.SpaceBetween) {
                            CoinDisplay("Or", display.gold, ColorsSystem.Yellow)
                            CoinDisplay("Argent", display.silver, ColorsSystem.TextSecondary)
                            CoinDisplay("Cuivre", display.copper,ColorsSystem.Orange)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Total : ${character.credits} crédits",
                            fontSize = 14.sp,
                            color = ColorsSystem.TextDisabled,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NunitoFontFamily,

                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryButton (
                        onClick = { showAddDialog = true },
                        modifier = Modifier.height(40.dp).weight(1f),
                        color = ColorsSystem.Orange,
                        borderColor = ColorsSystem.Orange.copy(0.35f)
                    ) {
                        Text(
                            text = "+ Ajouter",
                            fontSize = 13.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    PrimaryButton(
                        onClick = { showSpendDialog = true },
                        modifier = Modifier.height(40.dp).weight(1f),
                        color = ColorsSystem.TextSecondary,
                        borderColor = ColorsSystem.SecondBorder
                    ) {
                        Text(
                            text = "- Dépenser",
                            fontSize = 13.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                        )
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
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            fontFamily = NunitoFontFamily,
            color = color
        )
        Text(
            modifier = Modifier.offset(y = (-3).dp),
            text = label,
            fontSize = 16.sp,
            color = ColorsSystem.TextDisabled,
            fontWeight = FontWeight.Black,
            fontFamily = NunitoFontFamily,
            letterSpacing = 1.sp
        )
    }
}