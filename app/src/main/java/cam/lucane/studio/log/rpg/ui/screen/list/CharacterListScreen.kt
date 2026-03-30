package cam.lucane.studio.log.rpg.ui.screen.list

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.components.common.SessionBanner
import cam.lucane.studio.log.rpg.ui.components.common.buttons.DotButton
import cam.lucane.studio.log.rpg.ui.components.common.header.HomeHeader
import cam.lucane.studio.log.rpg.ui.dialog.character.CreateCharacterDialog
import cam.lucane.studio.log.rpg.ui.dialog.character.DeleteCharacterDialog
import cam.lucane.studio.log.rpg.ui.screen.list.components.CharacterCard
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterListViewModel
import cam.lucane.studio.log.rpg.ui.viewmodel.PlayerSessionViewModel

@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel,
    playerSessionViewModel: PlayerSessionViewModel,
    onNavigateToCharacter: (Long) -> Unit,
    onCreateSession: () -> Unit,
    onJoinSession: () -> Unit,
    onSwitchCharacter: () -> Unit
) {
    val context     = LocalContext.current
    val characters  by viewModel.characters.collectAsState()
    val isConnected by playerSessionViewModel.isConnected.collectAsState()
    val wasKicked   by playerSessionViewModel.wasKicked.collectAsState()
    val sharedChar  by playerSessionViewModel.sharedCharacter.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Character?>(null) }

    // ── Dialog "vous avez été kické" ─────────────────────────────────────
    if (wasKicked) {
        AlertDialog(
            onDismissRequest = { playerSessionViewModel.resetKick() },
            title = {
                Text("Session terminée", fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Text("Le Maître de Jeu vous a retiré de la session.", fontFamily = NunitoFontFamily)
            },
            confirmButton = {
                TextButton(onClick = { playerSessionViewModel.resetKick() }) {
                    Text("OK", fontWeight = FontWeight.ExtraBold, color = AccentPurple)
                }
            },
            containerColor = ColorsSystem.SurfaceDark
        )
    }

    // ── Import JSON ───────────────────────────────────────────────────────
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            val json = context.contentResolver.openInputStream(uri)
                ?.bufferedReader()?.readText() ?: return@let
            viewModel.importCharacter(
                json      = json,
                onSuccess = { Toast.makeText(context, "✅ Personnage importé", Toast.LENGTH_SHORT).show() },
                onError   = { Toast.makeText(context, "❌ Erreur d'importation", Toast.LENGTH_SHORT).show() }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
    ) {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                HomeHeader(
                    onImportClick   = {
                        importLauncher.launch(
                            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "application/json"
                            }
                        )
                    },
                    onCreateSession = onCreateSession,
                    onJoinSession   = onJoinSession,
                    isInSession     = isConnected
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    // ── Bannière session active (joueur) ──────────────────
                    item {
                        AnimatedVisibility(
                            visible = isConnected,
                            enter   = expandVertically() + fadeIn(),
                            exit    = shrinkVertically() + fadeOut()
                        ) {
                            SessionBanner(
                                label    = "EN SESSION",
                                subtitle = if (sharedChar?.name != null) "Partage actif : ${sharedChar?.name}"
                                else "Connecté · aucun personnage partagé",
                                onSwitch = onSwitchCharacter,
                                onQuit   = { playerSessionViewModel.disconnect() }
                            )
                        }
                    }

                    // ── Liste personnages ─────────────────────────────────
                    if (characters.isNotEmpty()) {
                        item {
                            Text(
                                text = "MES PERSONNAGES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ColorsSystem.TextDisabled,
                                letterSpacing = 1.5.sp,
                                fontFamily = NunitoFontFamily,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp, top = 4.dp, bottom = 2.dp)
                            )
                        }
                        items(characters, key = { it.id }) { character ->
                            CharacterCard(
                                character = character,
                                isShared  = isConnected && sharedChar?.id == character.id,
                                onClick   = { onNavigateToCharacter(character.id) },
                                onDelete  = { showDeleteDialog = character }
                            )
                        }
                    }

                    // ── Bouton ajouter ────────────────────────────────────
                    item {
                        DotButton(
                            modifier   = Modifier.fillMaxWidth(0.9f),
                            label      = "＋ Ajouter un personnage",
                            dashColor  = ColorsSystem.Green.copy(0.4f),
                            labelColor = ColorsSystem.GreenDark,
                            onClick    = { showCreateDialog = true }
                        )
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }

    // ── Dialogues ─────────────────────────────────────────────────────────
    if (showCreateDialog) {
        CreateCharacterDialog(
            onDismiss = { showCreateDialog = false },
            onCreated = { name ->
                viewModel.createCharacter(name) { id ->
                    showCreateDialog = false
                    onNavigateToCharacter(id)
                }
            }
        )
    }

    showDeleteDialog?.let { character ->
        DeleteCharacterDialog(
            character = character,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteCharacter(character)
                showDeleteDialog = null
            }
        )
    }
}