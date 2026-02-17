package cam.lucane.studio.log.rpg.ui.screen

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cam.lucane.studio.log.rpg.data.entity.getCurrencyDisplay
import cam.lucane.studio.log.rpg.ui.screen.tabs.*
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CharacterDetailViewModel = viewModel(
        factory = CharacterDetailViewModelFactory(
            application = context.applicationContext as android.app.Application,
            characterId = characterId
        )
    )
    val character by viewModel.character.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }

    val tabs = listOf("FICHE", "COMPT.", "SORTS", "INV.", "NOTES")
    val tabIcons = listOf(
        Icons.Default.Description,
        Icons.Default.Favorite,
        Icons.Default.AutoAwesome,
        Icons.Default.Backpack,
        Icons.Default.Notes
    )

    // Couleur d'accent du personnage (stable par ID)
    val accentColor = remember(characterId) {
        val colors = listOf(AccentRed, AccentPurple, AccentGreen, AccentGold)
        colors[(characterId % colors.size).toInt()]
    }

    // Export launcher
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.exportCharacter { json ->
                    context.contentResolver.openOutputStream(uri)?.use {
                        it.write(json.toByteArray())
                    }
                }
            }
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val outputFile = java.io.File(context.filesDir, "character_${characterId}_sheet.pdf")
                    inputStream?.use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    viewModel.updatePdf(outputFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Orbe ambiance en haut, couleur du personnage
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-150).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.20f), Color.Transparent)
                    )
                )
        )

        character?.let { char ->
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Header ──────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    // Bouton retour
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurface)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Retour",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Nom du personnage
                    Text(
                        text = char.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Menu
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassSurface)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                "Menu",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Exporter en JSON") },
                                leadingIcon = { Icon(Icons.Default.Upload, null) },
                                onClick = {
                                    showMenu = false
                                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_TITLE, "${char.name}.json")
                                    }
                                    exportLauncher.launch(intent)
                                }
                            )
                        }
                    }
                }

                // ── Tabs ─────────────────────────────────────────────────────
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = accentColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = accentColor,
                            height = 2.dp
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            selectedContentColor = accentColor,
                            unselectedContentColor = TextSecondary
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    tabIcons[index],
                                    title,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(title, fontSize = 9.sp, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(color = BorderSubtle)

                // ── Contenu de l'onglet ──────────────────────────────────────
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedTab) {
                        0 -> SheetTab(
                            character = char,
                            viewModel = viewModel,
                            onImportPdf = {
                                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "application/pdf"
                                }
                                pdfLauncher.launch(intent)
                            },
                            pdfLauncher = pdfLauncher
                        )
                        1 -> CountersTab(char, viewModel)
                        2 -> AbilitiesTab(characterId, viewModel)
                        3 -> InventoryTab(characterId, viewModel)
                        4 -> NotesTab(char, viewModel)
                    }
                }
            }
        } ?: run {
            // Loading
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AccentPurple
            )
        }
    }
}

// ── Onglet Fiche PDF ─────────────────────────────────────────────────────────

@Composable
fun SheetTab(
    character: cam.lucane.studio.log.rpg.data.entity.Character,
    viewModel: CharacterDetailViewModel,
    pdfLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onImportPdf: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val pdfFile = character.pdfPath?.let { java.io.File(it) }

        if (pdfFile != null && pdfFile.exists()) {
            PdfViewer(
                pdfFile = pdfFile,
                pdfLauncher = pdfLauncher
            )
        } else {
            // État vide
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📄", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Aucune fiche PDF",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Importez votre fiche de personnage",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onImportPdf,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Icon(Icons.Default.Upload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importer un PDF")
                }
            }
        }
    }
}

// ── Onglet Compteurs ─────────────────────────────────────────────────────────

// ── Onglet Notes (placeholder — sera remplacé par NotesComponents) ───────────

@Composable
fun NotesTab(
    character: cam.lucane.studio.log.rpg.data.entity.Character,
    viewModel: CharacterDetailViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Bientôt disponible…", color = TextSecondary)
    }
}

// Extension nécessaire pour TabRow indicator
private fun Modifier.tabIndicatorOffset(
    tabPosition: TabPosition
): Modifier = this.then(
    Modifier
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = tabPosition.left)
        .width(tabPosition.width)
)