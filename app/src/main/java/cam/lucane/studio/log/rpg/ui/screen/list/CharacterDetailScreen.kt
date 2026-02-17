package cam.lucane.studio.log.rpg.ui.screen.list

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.abilities.AbilitiesTab
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters.CountersTab
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.inventory.InventoryTab
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.NotesTab
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.sheet.SheetTab
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.BackgroundDark
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.GlassSurface
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModelFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CharacterDetailViewModel = viewModel(
        factory = CharacterDetailViewModelFactory(
            application = context.applicationContext as Application,
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
                    val outputFile = File(context.filesDir, "character_${characterId}_sheet.pdf")
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

// Extension nécessaire pour TabRow indicator
private fun Modifier.tabIndicatorOffset(
    tabPosition: TabPosition
): Modifier = this.then(
    Modifier
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = tabPosition.left)
        .width(tabPosition.width)
)