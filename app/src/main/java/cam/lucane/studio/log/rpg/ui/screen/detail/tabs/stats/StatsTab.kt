package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cam.lucane.studio.log.rpg.ui.components.common.buttons.DotButton
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.utils.getAccentColorByCharacterId
import cam.lucane.studio.log.rpg.ui.viewmodel.StatsViewModel

/**
 * Onglet Caractéristiques du personnage.
 * Suit le même pattern que CountersTab : reçoit characterId, crée son propre ViewModel.
 */
@Composable
fun StatsTab(characterId: Long) {
    val context = LocalContext.current
    val viewModel: StatsViewModel = viewModel(
        key     = "stats_$characterId",
        factory = StatsViewModel.factory(characterId, context.applicationContext as Application),
    )
    val ui by viewModel.uiState.collectAsState()
    val mainColor = getAccentColorByCharacterId(characterId)
    Box(modifier = Modifier.fillMaxSize().background(ColorsSystem.BackgroundApp)) {

        LazyColumn(
            modifier        = Modifier.fillMaxSize(),
            contentPadding  = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(ui.sections, key = { it.section.id }) { data ->
                val isThisEditing  = ui.editingSectionId == data.section.id
                val anotherEditing = ui.editingSectionId != null && !isThisEditing

                StatSectionCard(
                    data = data,
                    isEditing = isThisEditing,
                    onEditToggle = {
                        if (isThisEditing) viewModel.stopEdit()
                        else               viewModel.startEdit(data.section.id)
                    },
                    onSectionTitleChange = { viewModel.updateSectionTitle(data.section.id, it) },
                    onDeleteSection = { viewModel.deleteSection(data.section) },
                    onWidgetChange = viewModel::updateWidget,
                    onWidgetDelete = viewModel::deleteWidget,
                    onAddWidget = { viewModel.openAddWidgetSheet(data.section.id) },
                    mainColor = mainColor,
                    modifier             = Modifier.alpha(if (anotherEditing) 0.4f else 1f),
                )
            }

            // Fantôme "nouvelle section" visible uniquement hors mode édition
            if (ui.editingSectionId == null) {
                item {
                    DotButton(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        label = "＋ Ajouter une section",
                        dashColor = mainColor.copy(0.4f),
                        labelColor = mainColor,
                        onClick = viewModel::openAddSectionSheet
                    )
                }
            }
        }
    }

    // ── Bottom sheets ────────────────────────────────────────────────────────

    if (ui.showAddSectionSheet) {
        AddSectionBottomSheet(
            onDismiss = viewModel::closeAddSectionSheet,
            onConfirm = viewModel::addSection,
        )
    }

    if (ui.showAddWidgetSheet) {
        val title = ui.sections.firstOrNull { it.section.id == ui.targetSectionId }?.section?.title.orEmpty()
        AddWidgetBottomSheet(
            sectionTitle = title,
            onDismiss    = viewModel::closeAddWidgetSheet,
            onConfirm    = viewModel::addWidget,
        )
    }
}
