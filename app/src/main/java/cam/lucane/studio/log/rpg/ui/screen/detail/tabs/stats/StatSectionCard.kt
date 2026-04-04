package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.StatWidget
import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor
import cam.lucane.studio.log.rpg.data.repository.StatSectionWithWidgets
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

private val CardShape = RoundedCornerShape(18.dp)
private val ChipShape = RoundedCornerShape(99.dp)
private const val COLS = 3

/**
 * Carte d'une section de caractéristiques.
 *
 * Normal  → widgets en lecture, bouton "Éditer".
 * Édition → widgets avec champs valeur éditables, bouton "✓ Terminer".
 *
 * Long-press sur le titre de la section → [SectionDeepEditModal] (titre + suppression).
 * Long-press sur un widget en édition   → [WidgetDeepEditModal]  (titre + couleur + suppression).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatSectionCard(
    data: StatSectionWithWidgets,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    onSectionTitleChange: (String) -> Unit,
    onDeleteSection: () -> Unit,
    onWidgetChange: (StatWidget) -> Unit,
    onWidgetDelete: (StatWidget) -> Unit,
    onAddWidget: () -> Unit,
    mainColor: Color,
    modifier: Modifier = Modifier,
) {
    // ── État local des modals ─────────────────────────────────────────────────
    var showSectionModal by remember { mutableStateOf(false) }
    var deepEditWidget   by remember { mutableStateOf<StatWidget?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(ColorsSystem.BackgroundCard)
            .border(
                1.5.dp,
                if (isEditing) ColorsSystem.Green.copy(.45f) else ColorsSystem.BackgroundCard,
                CardShape,
            ),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Titre — long press ouvre la modal d'édition profonde
            Text(
                text          = data.section.title.uppercase(),
                fontSize      = 10.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = ColorsSystem.TextDisabled,
                letterSpacing = 1.5.sp,
                modifier      = Modifier.combinedClickable(
                    onClick     = {},
                    onLongClick = { showSectionModal = true },
                ),
            )

            if (!isEditing) {
                CardOptionButton(
                    modifier = Modifier.size(26.dp),
                    onClick = onEditToggle,
                    color = mainColor
                ) {
                    Text(
                        text = "✏️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = NunitoFontFamily
                    )
                }
            }
        }

        // ── Grille de widgets ─────────────────────────────────────────────────
        WidgetGrid(
            widgets        = data.widgets,
            isEditing      = isEditing,
            onWidgetChange = onWidgetChange,
            onAddWidget    = onAddWidget,
            onWidgetLongPress = { deepEditWidget = it },
        )

        if (isEditing) DoneButton(onClick = onEditToggle)
    }

    // ── Modal édition section (titre + suppression) ───────────────────────────
    if (showSectionModal) {
        SectionDeepEditModal(
            currentTitle = data.section.title,
            onDismiss    = { showSectionModal = false },
            onConfirm    = { newTitle ->
                onSectionTitleChange(newTitle)
                showSectionModal = false
            },
            onDelete = {
                showSectionModal = false
                onDeleteSection()
            },
        )
    }

    // ── Modal édition profonde widget (titre + couleur + suppression) ─────────
    deepEditWidget?.let { w ->
        WidgetDeepEditModal(
            widget    = w,
            onDismiss = { deepEditWidget = null },
            onConfirm = { newTitle, newColor ->
                onWidgetChange(w.copy(title = newTitle, accentColor = newColor.name))
                deepEditWidget = null
            },
            onDelete  = {
                deepEditWidget = null
                onWidgetDelete(w)
            },
        )
    }
}

@Composable
private fun WidgetGrid(
    widgets: List<StatWidget>,
    isEditing: Boolean,
    onWidgetChange: (StatWidget) -> Unit,
    onAddWidget: () -> Unit,
    onWidgetLongPress: (StatWidget) -> Unit,
) {
    val totalItems = widgets.size + if (isEditing) 1 else 0
    val rows = (totalItems + COLS - 1) / COLS

    Column(
        modifier            = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(COLS) { col ->
                    val index   = row * COLS + col
                    val isGhost = isEditing && index == widgets.size
                    when {
                        isGhost              -> AddWidgetGhost(onClick = onAddWidget, modifier = Modifier.weight(1f))
                        index < widgets.size -> {
                            val w = widgets[index]
                            if (isEditing)
                                StatWidgetEditItem(
                                    widget         = w,
                                    onWidgetChange = onWidgetChange,
                                    onLongPress    = { onWidgetLongPress(w) },
                                    modifier       = Modifier.weight(1f),
                                )
                            else
                                StatWidgetReadItem(widget = w, modifier = Modifier.weight(1f))
                        }
                        else                 -> Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun AddWidgetGhost(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .heightIn(min = 82.dp)
            .clip(shape)
            .border(1.5.dp, ColorsSystem.Green.copy(.4f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("＋", fontSize = 20.sp, color = ColorsSystem.GreenDark.copy(.55f))
            Text("Ajouter", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.GreenDark.copy(.55f))
        }
    }
}

@Composable
private fun DoneButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(ColorsSystem.Green)
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("✓ Terminer l'édition", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
    }
}