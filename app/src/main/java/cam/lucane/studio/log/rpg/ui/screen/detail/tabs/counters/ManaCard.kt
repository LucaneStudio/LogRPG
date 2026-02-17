package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.dialog.counters.EditMaxDialog
import cam.lucane.studio.log.rpg.ui.theme.ManaBlue
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

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