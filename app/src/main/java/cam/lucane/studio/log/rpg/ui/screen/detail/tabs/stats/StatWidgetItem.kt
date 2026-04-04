package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.StatWidget
import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor
import cam.lucane.studio.log.rpg.data.entity.WidgetType
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

private val CardShape = RoundedCornerShape(14.dp)
private val PillShape = RoundedCornerShape(99.dp)

// ── Conteneur commun ─────────────────────────────────────────────────────────

@Composable
private fun WidgetCard(
    isEditing: Boolean,
    accentColor: Color,
    accentLight: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Lecture : bordure accent légère + tint de fond subtil
    // Édition  : bordure accent plus marquée + tint de fond plus prononcé
    val bgColor     = if (isEditing) accentLight.copy(.18f) else accentLight.copy(.10f)
    val borderColor = if (isEditing) accentColor.copy(.50f) else accentColor.copy(.25f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(bgColor)
            .border(1.5.dp, borderColor, CardShape)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        content = content,
    )
}

@Composable
private fun WidgetTitle(title: String) {
    Text(
        text          = title.uppercase(),
        fontSize      = 9.sp,
        fontWeight    = FontWeight.ExtraBold,
        color         = ColorsSystem.TextDisabled,
        letterSpacing = 0.8.sp,
        textAlign     = TextAlign.Center,
    )
}

// ── Lecture seule ─────────────────────────────────────────────────────────────

@Composable
fun StatWidgetReadItem(widget: StatWidget, modifier: Modifier = Modifier) {
    val accent = widget.widgetAccentColor()
    WidgetCard(isEditing = false, accentColor = accent.main, accentLight = accent.light, modifier = modifier) {
        WidgetTitle(widget.title)
        when (widget.widgetType()) {
            WidgetType.CAR_MOD -> CarModRead(widget, accent.main)
            WidgetType.FREE    -> FreeRead(widget)
            WidgetType.PERCENT -> PercentRead(widget, accent.main)
        }
    }
}

@Composable
private fun CarModRead(widget: StatWidget, accent: Color) {
    val accentEnum = widget.widgetAccentColor()
    // Le jaune est illisible en texte — on utilise l'orange à la place
    val textColor = if (accentEnum == WidgetAccentColor.YELLOW) ColorsSystem.Orange else accent

    Text(widget.value.ifBlank { "—" }, fontSize = 28.sp, fontWeight = FontWeight.Black, color = ColorsSystem.TextPrimary)
    if (widget.modifier.isNotBlank()) {
        Box(
            Modifier.clip(PillShape).background(accentEnum.light)
                .border(1.5.dp, textColor.copy(.3f), PillShape)
                .padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(widget.modifier, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
        }
    }
}

@Composable
private fun FreeRead(widget: StatWidget) {
    Text(widget.value.ifBlank { "—" }, fontSize = 30.sp, fontWeight = FontWeight.Black, color = ColorsSystem.TextPrimary)
}

@Composable
private fun PercentRead(widget: StatWidget, accent: Color) {
    PercentRing(percent = widget.value.toIntOrNull()?.coerceIn(0, 100) ?: 0, accent = accent)
}

// ── Édition (valeurs uniquement) ──────────────────────────────────────────────

/**
 * Widget en mode édition classique : affiche les champs de valeur éditables.
 * Le titre et la couleur s'éditent via un long-press ([onLongPress]).
 *
 * Curseur stable : état local synchronisé uniquement si la valeur change
 * depuis une source externe (pas lors de la frappe de l'utilisateur).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatWidgetEditItem(
    widget: StatWidget,
    onWidgetChange: (StatWidget) -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = widget.widgetAccentColor()

    // États locaux — fix saut de curseur
    var localValue    by remember(widget.id) { mutableStateOf(widget.value) }
    var localModifier by remember(widget.id) { mutableStateOf(widget.modifier) }
    LaunchedEffect(widget.value)    { if (localValue    != widget.value)    localValue    = widget.value }
    LaunchedEffect(widget.modifier) { if (localModifier != widget.modifier) localModifier = widget.modifier }

    Box(modifier = modifier) {
        WidgetCard(
            isEditing   = true,
            accentColor = accent.main,
            accentLight = accent.light,
            modifier    = Modifier.combinedClickable(
                onClick     = {},
                onLongClick = onLongPress,
            ),
        ) {
            WidgetTitle(widget.title)
            Spacer(Modifier.height(2.dp))

            when (widget.widgetType()) {
                WidgetType.CAR_MOD -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CombatTextField(
                            value         = localValue,
                            onValueChange = { localValue = it; onWidgetChange(widget.copy(value = it)) },
                            label         = "Valeur",
                            modifier      = Modifier.fillMaxWidth(),
                        )
                        CombatTextField(
                            value         = localModifier,
                            onValueChange = { localModifier = it; onWidgetChange(widget.copy(modifier = it)) },
                            label         = "Modificateur",
                            modifier      = Modifier.fillMaxWidth(),
                        )
                    }
                }
                WidgetType.FREE -> {
                    CombatTextField(
                        value         = localValue,
                        onValueChange = { localValue = it; onWidgetChange(widget.copy(value = it)) },
                        label         = "Valeur",
                        modifier      = Modifier.fillMaxWidth(),
                    )
                }
                WidgetType.PERCENT -> {
                    PercentRing(
                        percent = localValue.toIntOrNull()?.coerceIn(0, 100) ?: 0,
                        accent  = accent.main,
                    )
                    CombatTextField(
                        value           = localValue,
                        onValueChange   = { localValue = it; onWidgetChange(widget.copy(value = it)) },
                        label           = "% (0–100)",
                        modifier        = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            }
        }

    }
}