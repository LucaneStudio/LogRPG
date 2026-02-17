package cam.lucane.studio.log.rpg.ui.dialog.counters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cam.lucane.studio.log.rpg.data.entity.CurrencyMode

@Composable
fun EditMaxDialog(
    title: String,
    currentValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var value by remember { mutableStateOf(currentValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Valeur maximum") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newValue = value.toIntOrNull() ?: currentValue
                    onConfirm(newValue)
                },
                enabled = value.toIntOrNull() != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun CurrencyInputDialog(
    title: String,
    mode: CurrencyMode,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var gold by remember { mutableStateOf("0") }
    var silver by remember { mutableStateOf("0") }
    var copper by remember { mutableStateOf("0") }

    val totalCredits = when (mode) {
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (mode) {
                    CurrencyMode.SINGLE -> {
                        OutlinedTextField(
                            value = copper,
                            onValueChange = { copper = it },
                            label = { Text("Crédits") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    CurrencyMode.BY_TEN, CurrencyMode.BY_HUNDRED -> {
                        OutlinedTextField(
                            value = gold,
                            onValueChange = { gold = it },
                            label = { Text("Or (Po)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = {
                                Text(
                                    if (mode == CurrencyMode.BY_TEN) "1 Po = 100 crédits"
                                    else "1 Po = 10000 crédits"
                                )
                            },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = silver,
                            onValueChange = { silver = it },
                            label = { Text("Argent (Pa)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = {
                                Text(
                                    if (mode == CurrencyMode.BY_TEN) "1 Pa = 10 crédits"
                                    else "1 Pa = 100 crédits"
                                )
                            },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = copper,
                            onValueChange = { copper = it },
                            label = { Text("Cuivre (Pc)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = { Text("1 Pc = 1 crédit") },
                            singleLine = true
                        )

                        Divider()
                        Text(
                            "Total: $totalCredits crédits",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(totalCredits) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun CurrencyModeDialog(
    currentMode: CurrencyMode,
    onDismiss: () -> Unit,
    onConfirm: (CurrencyMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mode de monnaie") },
        text = {
            Column {
                CurrencyMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(mode) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = mode == currentMode,
                            onClick = { onConfirm(mode) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                when (mode) {
                                    CurrencyMode.SINGLE -> "Monnaie unique"
                                    CurrencyMode.BY_TEN -> "Conversion x10"
                                    CurrencyMode.BY_HUNDRED -> "Conversion x100"
                                }
                            )
                            Text(
                                when (mode) {
                                    CurrencyMode.SINGLE -> "Une seule monnaie"
                                    CurrencyMode.BY_TEN -> "10 cuivre = 1 argent, 10 argent = 1 or"
                                    CurrencyMode.BY_HUNDRED -> "100 cuivre = 1 argent, 100 argent = 1 or"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}