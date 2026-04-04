package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor
import cam.lucane.studio.log.rpg.data.entity.WidgetType
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWidgetBottomSheet(
    sectionTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, type: WidgetType, color: WidgetAccentColor) -> Unit,
) {
    var title  by remember { mutableStateOf("") }
    var type   by remember { mutableStateOf(WidgetType.CAR_MOD) }
    var color  by remember { mutableStateOf(WidgetAccentColor.PURPLE) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = ColorsSystem.BackgroundCard,
        dragHandle = {
            Box(
                Modifier.padding(vertical = 14.dp)
                    .width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(ColorsSystem.SecondBorder)
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Nouveau widget", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary)
                Text("Section : $sectionTitle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorsSystem.TextSecondary)
            }

            TypeSelector(selected = type, onSelect = { type = it })

            CombatTextField(
                value         = title,
                onValueChange = { title = it },
                label         = "Titre du widget",
                modifier      = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("COULEUR D'ACCENT")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WidgetAccentColor.entries.forEach { c ->
                        ColorDot(c, selected = c == color, onClick = { color = c })
                    }
                }
            }

            Button(
                onClick  = { onConfirm(title, type, color) },
                enabled  = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(99.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
            ) {
                Text("Ajouter le widget", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

// ── Type selector ─────────────────────────────────────────────────────────────

@Composable
private fun TypeSelector(selected: WidgetType, onSelect: (WidgetType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        WidgetType.entries.forEach { t ->
            TypeCard(t, isSelected = t == selected, onClick = { onSelect(t) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TypeCard(type: WidgetType, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(13.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) ColorsSystem.GreenLight else ColorsSystem.BackgroundSurface)
            .border(1.5.dp, if (isSelected) ColorsSystem.Green else ColorsSystem.Divider, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        TypePreview(type)
        Text(
            type.label,
            fontSize   = 9.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = if (isSelected) ColorsSystem.GreenDark else ColorsSystem.TextSecondary,
        )
    }
}

@Composable
private fun TypePreview(type: WidgetType) {
    Box(
        modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(
            when (type) {
                WidgetType.CAR_MOD -> ColorsSystem.PurpleLight
                WidgetType.FREE    -> ColorsSystem.GreenLight
                WidgetType.PERCENT -> ColorsSystem.BlueLight
            }
        ),
        contentAlignment = Alignment.Center,
    ) {
        when (type) {
            WidgetType.CAR_MOD -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("18", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ColorsSystem.Purple, lineHeight = 16.sp)
                Text("+4", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ColorsSystem.Purple.copy(.7f))
            }
            WidgetType.FREE    -> Text("42", fontSize = 15.sp, fontWeight = FontWeight.Black, color = ColorsSystem.GreenDark)
            WidgetType.PERCENT -> PercentRing(percent = 70, accent = ColorsSystem.Blue, size = 28)
        }
    }
}

private val WidgetType.label get() = when (this) {
    WidgetType.CAR_MOD -> "Car + Mod"
    WidgetType.FREE    -> "Valeur libre"
    WidgetType.PERCENT -> "Pourcentage"
}

// ── Couleur ───────────────────────────────────────────────────────────────────

@Composable
private fun ColorDot(color: WidgetAccentColor, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(24.dp).clip(CircleShape).background(color.main)
            .then(if (selected) Modifier.border(3.dp, ColorsSystem.TextPrimary, CircleShape) else Modifier)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextDisabled, letterSpacing = 0.8.sp)
}
