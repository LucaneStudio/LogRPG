package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.dialog.counters.EditMaxDialog
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.HealthRed
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

@Composable
fun HealthCard(character: Character, viewModel: CharacterDetailViewModel) {
    var showEditMaxDialog by remember { mutableStateOf(false) }

    StatCounterCard(
        label = "❤\uFE0F POINTS DE VIE",
        current = character.currentHealth,
        max = character.maxHealth,
        mainColor = ColorsSystem.Red,
        backgroundMainColor = ColorsSystem.RedLight,
        mainBrush = ColorsSystem.GradientBarHealth,
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
            },
            emoji = "❤️",
            accentColor = ColorsSystem.Red
        )
    }
}