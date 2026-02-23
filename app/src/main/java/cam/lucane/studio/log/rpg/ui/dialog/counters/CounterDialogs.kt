package cam.lucane.studio.log.rpg.ui.dialog.counters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.CurrencyMode
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetButtonRow
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetDivider
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetLabel
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

// ── EditMaxDialog ────────────────────────────────────────────────────────

@Composable
fun EditMaxDialog(
    title: String,
    emoji: String,
    accentColor: androidx.compose.ui.graphics.Color,
    currentValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var value by remember { mutableStateOf(currentValue.toString()) }
    val parsed = value.toIntOrNull()

    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "$emoji $title",
        subtitle = "Valeur actuelle : $currentValue"
    ) {
        // Grand champ numérique coloré
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = accentColor.copy(.08f),
                unfocusedContainerColor = accentColor.copy(.08f),
                cursorColor = accentColor
            )
        )

        Spacer(Modifier.height(12.dp))

        // Boutons rapides
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(-10, -5, -1, +1, +5, +10).forEach { delta ->
                val isPositive = delta > 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isPositive) accentColor.copy(.1f) else ColorsSystem.BackgroundSurface
                        )
                        .border(
                            1.5.dp,
                            if (isPositive) accentColor.copy(.3f) else ColorsSystem.Divider,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            val current = value.toIntOrNull() ?: currentValue
                            value = (current + delta).coerceAtLeast(0).toString()
                        }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isPositive) "+$delta" else "$delta",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily,
                        color = if (isPositive) accentColor else ColorsSystem.TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        SheetButtonRow(
            onDismiss = onDismiss,
            onConfirm = { onConfirm(parsed ?: currentValue) },
            confirmEnabled = parsed != null && parsed >= 0,
            confirmLabel = "Valider"
        )
    }
}

// ── CurrencyModeDialog ───────────────────────────────────────────────────

@Composable
fun CurrencyModeDialog(
    currentMode: CurrencyMode,
    onDismiss: () -> Unit,
    onConfirm: (CurrencyMode) -> Unit
) {
    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "Mode de monnaie"
    ) {
        data class ModeOption(
            val mode: CurrencyMode,
            val label: String,
            val desc: String,
            val emoji: String
        )

        val options = listOf(
            ModeOption(CurrencyMode.SINGLE,     "Monnaie unique",   "Une seule valeur", "🪙"),
            ModeOption(CurrencyMode.BY_TEN,     "Conversion ×10",   "10 cuivre = 1 argent, 10 argent = 1 or", "🥈"),
            ModeOption(CurrencyMode.BY_HUNDRED, "Conversion ×100",  "100 cuivre = 1 argent, 100 argent = 1 or", "🥇"),
        )

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            options.forEachIndexed { i, opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onConfirm(opt.mode) }
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Indicateur sélection
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (opt.mode == currentMode) ColorsSystem.Yellow
                                else Color.Transparent
                            )
                            .border(
                                2.5.dp,
                                if (opt.mode == currentMode) ColorsSystem.Yellow else ColorsSystem.TextDisabled,
                                CircleShape
                            )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opt.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                            color = ColorsSystem.TextPrimary
                        )
                        Text(
                            text = opt.desc,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NunitoFontFamily,
                            color = ColorsSystem.TextDisabled
                        )
                    }
                    Text(opt.emoji, fontSize = 20.sp)
                }
                if (i < options.size - 1) SheetDivider()
            }
        }

        Spacer(Modifier.height(20.dp))

        // Bouton fermer seul
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(99.dp))
                .background(ColorsSystem.BackgroundSurface)
                .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                .clickable { onDismiss() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fermer",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color = ColorsSystem.TextSecondary
            )
        }
    }
}

// ── CurrencyInputDialog ──────────────────────────────────────────────────

@Composable
fun CurrencyInputDialog(
    title: String,
    mode: CurrencyMode,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var gold   by remember { mutableStateOf("0") }
    var silver by remember { mutableStateOf("0") }
    var copper by remember { mutableStateOf("0") }

    val total = when (mode) {
        CurrencyMode.SINGLE -> copper.toIntOrNull() ?: 0
        CurrencyMode.BY_TEN -> {
            val g = gold.toIntOrNull() ?: 0
            val s = silver.toIntOrNull() ?: 0
            val c = copper.toIntOrNull() ?: 0
            (g * 100) + (s * 10) + c
        }
        CurrencyMode.BY_HUNDRED -> {
            val g = gold.toIntOrNull() ?: 0
            val s = silver.toIntOrNull() ?: 0
            val c = copper.toIntOrNull() ?: 0
            (g * 10000) + (s * 100) + c
        }
    }

    BaseBottomSheet(onDismiss = onDismiss, title = title) {

        @Composable
        fun CurrencyField(label: String, emoji: String, value: String, onChange: (String) -> Unit, hint: String? = null) {
            SheetLabel(text = label)
            Spacer(Modifier.height(5.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0", fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled) },
                leadingIcon = { Text(emoji, fontSize = 16.sp) },
                supportingText = if (hint != null) { { Text(hint, fontFamily = NunitoFontFamily, fontSize = 10.sp, color = ColorsSystem.TextDisabled) } } else null,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorsSystem.Yellow,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = ColorsSystem.BackgroundCard,
                    unfocusedContainerColor = ColorsSystem.BackgroundCard,
                    cursorColor = ColorsSystem.Yellow
                ),
                textStyle = TextStyle(fontFamily = NunitoFontFamily, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ColorsSystem.TextPrimary)
            )
            Spacer(Modifier.height(10.dp))
        }

        when (mode) {
            CurrencyMode.SINGLE -> {
                CurrencyField("CRÉDITS", "🪙", copper, { copper = it })
            }
            CurrencyMode.BY_TEN, CurrencyMode.BY_HUNDRED -> {
                val ratioLabel = if (mode == CurrencyMode.BY_TEN) "×10" else "×100"
                CurrencyField("OR (PO)", "🥇", gold, { gold = it }, "1 Po = ${if (mode == CurrencyMode.BY_TEN) "100" else "10 000"} crédits")
                CurrencyField("ARGENT (PA)", "🥈", silver, { silver = it }, "1 Pa = ${if (mode == CurrencyMode.BY_TEN) "10" else "100"} crédits")
                CurrencyField("CUIVRE (PC)", "🪙", copper, { copper = it }, "1 Pc = 1 crédit")

                SheetDivider()
                Spacer(Modifier.height(12.dp))

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
                    Text(
                        text = "$total crédits",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = NunitoFontFamily,
                        color = ColorsSystem.Yellow
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        SheetButtonRow(
            onDismiss = onDismiss,
            onConfirm = { onConfirm(total) },
            confirmLabel = "Valider"
        )
    }
}
