package cam.lucane.studio.log.rpg.ui.screen.list

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.components.common.buttons.DotButton
import cam.lucane.studio.log.rpg.ui.components.common.header.HomeHeader
import cam.lucane.studio.log.rpg.ui.dialog.character.CreateCharacterDialog
import cam.lucane.studio.log.rpg.ui.screen.list.components.CharacterCard
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterListViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

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
            .background(ColorsSystem.BackgroundApp)
    ) {
        // ── Contenu principal ────────────────────────────────────────────
        Scaffold(
            modifier = Modifier.haze(hazeState).systemBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                HomeHeader(
                    onImportClick = {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/json"
                        }
                        importLauncher.launch(intent)
                    }
                )
            }
        ) { padding ->
            if (characters.isEmpty()) {
                CharacterEmptyState(
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item{
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                            text = "MES PERSONNAGES",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = NunitoFontFamily,
                            textAlign = TextAlign.Start,
                            color = ColorsSystem.TextDisabled,
                            letterSpacing = (1.5).sp
                        )
                    }
                    items(characters, key = { it.id }) { character ->
                        CharacterCard(
                            character = character,
                            onClick = { onNavigateToCharacter(character.id) },
                            onDelete = { showDeleteDialog = character }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(1.dp)) }
                    item {
                        DotButton(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            label = "＋ Ajouter un personnage",
                            dashColor = ColorsSystem.Green.copy(0.4f),
                            labelColor = ColorsSystem.GreenDark,
                            onClick = { showCreateDialog = true }
                        )
                    }
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

@Composable
private fun CharacterEmptyState(
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
            fontSize = 15.sp,
            fontFamily = NunitoFontFamily,
            color = ColorsSystem.TextPrimary,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Créez votre premier aventurier",
            fontSize = 14.sp,
            fontFamily = NunitoFontFamily,
            color = ColorsSystem.TextSecondary,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onCreateClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green)
        ) {
            Text("＋ Ajouter un personnage")
        }
    }
}
