package cam.lucane.studio.log.rpg.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterListViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(onNavigateToCharacter: (Long) -> Unit) {
    val context = LocalContext.current
    val viewModel: CharacterListViewModel = viewModel()
    val characters by viewModel.characters.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Character?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    val hazeState = remember { HazeState() }

    // Launcher pour importer un personnage
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            val json = context.contentResolver.openInputStream(uri)
                ?.bufferedReader()?.readText() ?: return@let
            viewModel.importCharacter(
                json = json,
                onSuccess = { characterId ->
                    Toast.makeText(context, "✅ Personnage importé", Toast.LENGTH_SHORT).show()
                },
                onError = {
                    Toast.makeText(context, "❌ Erreur d'importation", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ── Orbes d'ambiance ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 150.dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentPurple.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-60).dp, y = 500.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentGreen.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )

        // ── Contenu principal ────────────────────────────────────────────
        Scaffold(
            modifier = Modifier.haze(hazeState),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "LogRPG",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurpleLight
                        )
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Menu", tint = TextSecondary)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Importer un personnage") },
                                leadingIcon = { Icon(Icons.Default.Download, null) },
                                onClick = {
                                    showMenu = false
                                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "application/json"
                                    }
                                    importLauncher.launch(intent)
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = AccentPurple,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Add, "Nouveau personnage", modifier = Modifier.size(24.dp))
                }
            }
        ) { padding ->
            if (characters.isEmpty()) {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    onCreateClick = { showCreateDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(characters, key = { it.id }) { character ->
                        CharacterCard(
                            character = character,
                            onClick = { onNavigateToCharacter(character.id) },
                            onDelete = { showDeleteDialog = character }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // ── Dialogues ────────────────────────────────────────────────────────
    if (showCreateDialog) {
        CreateCharacterDialog(
            onDismiss = { showCreateDialog = false },
            onCreated = { name ->
                viewModel.createCharacter(name) { characterId ->
                    onNavigateToCharacter(characterId)
                }
                showCreateDialog = false
            }
        )
    }

    showDeleteDialog?.let { character ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Supprimer ${character.name} ?") },
            text = { Text("Cette action est irréversible. Toutes les données seront perdues.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCharacter(character)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Supprimer", color = HealthRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Couleur d'accent basée sur le nom (pour différencier les cartes)
    val accentColor = remember(character.id) {
        val colors = listOf(AccentRed, AccentPurple, AccentGreen, AccentGold)
        colors[(character.id % colors.size).toInt()]
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Orbe d'ambiance dans la carte
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header : Avatar + nom + bouton supprimer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = character.name.firstOrNull()?.toString() ?: "?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = character.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Appuyer pour ouvrir",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Supprimer",
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barres PV et Mana
                HealthManaBar(
                    label = "PV",
                    current = character.currentHealth,
                    max = character.maxHealth,
                    color = HealthRed
                )
                Spacer(modifier = Modifier.height(6.dp))
                HealthManaBar(
                    label = "PM",
                    current = character.currentMana,
                    max = character.maxMana,
                    color = ManaBlue
                )
            }
        }
    }
}

@Composable
private fun HealthManaBar(
    label: String,
    current: Int,
    max: Int,
    color: Color
) {
    val progress = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(20.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.07f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )
        }

        Text(
            text = "$current/$max",
            fontSize = 10.sp,
            color = TextSecondary,
            modifier = Modifier.width(40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚔️", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Aucun personnage",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Créez votre premier aventurier",
            fontSize = 14.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Créer un personnage")
        }
    }
}

@Composable
private fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCreated: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau personnage") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom du personnage") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreated(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}