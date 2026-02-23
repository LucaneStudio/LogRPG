package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.runtime.*
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.ManaMode
import cam.lucane.studio.log.rpg.ui.dialog.counters.EditMaxDialog
import cam.lucane.studio.log.rpg.ui.dialog.counters.ManaModeDialog
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.ManaBlue
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

// ManaCard orchestre les deux modes via StatCounterCard ou SpellSlotsCard.
// Le changement de mode se fait via ManaModeDialog (bouton ⚙).

@Composable
fun ManaCard(character: Character, viewModel: CharacterDetailViewModel) {
    var showModeDialog by remember { mutableStateOf(false) }
    var showEditMaxDialog by remember { mutableStateOf(false) }


    when (character.manaMode) {
        ManaMode.MANA -> {
            StatCounterCard(
                label = "\uD83D\uDCA7 MANA",
                current = character.currentMana,
                max = character.maxMana,
                mainColor = ColorsSystem.Blue,
                backgroundMainColor = ColorsSystem.BlueLight,
                mainBrush = ColorsSystem.GradientBarMana,
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
                onEditMax = { showEditMaxDialog = true },
                onEditMode = { showModeDialog = true }
            )
        }

        ManaMode.SPELL_SLOTS -> {
            SpellSlotsCard(
                character = character,
                viewModel = viewModel,
                onEditMode = { showModeDialog = true }
            )
        }
    }

    if (showModeDialog) {
        ManaModeDialog(
            currentMode = character.manaMode,
            onDismiss = { showModeDialog = false },
            onConfirm = { mode ->
                viewModel.updateManaMode(mode)
                showModeDialog = false
            }
        )
    }

    if (showEditMaxDialog) {
        EditMaxDialog(
            title = "Mana Maximum",
            currentValue = character.maxMana,
            onDismiss = { showEditMaxDialog = false },
            onConfirm = { newMax ->
                viewModel.updateMana(character.currentMana, newMax)
                showEditMaxDialog = false
            },
            emoji = "💧",
            accentColor = ColorsSystem.Blue,
        )
    }
}