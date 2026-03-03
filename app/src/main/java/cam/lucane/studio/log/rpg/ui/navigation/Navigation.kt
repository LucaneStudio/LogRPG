package cam.lucane.studio.log.rpg.ui.navigation

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cam.lucane.studio.log.rpg.ui.screen.list.CharacterDetailScreen
import cam.lucane.studio.log.rpg.ui.screen.list.CharacterListScreen
import cam.lucane.studio.log.rpg.ui.screen.qr.MultiQrImportPreviewScreen
import cam.lucane.studio.log.rpg.ui.screen.qr.MultiQrScannerScreen
import cam.lucane.studio.log.rpg.ui.screen.qr.MultiQrScreen
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModelFactory
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterListViewModel

object Routes {
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL = "character_detail/{characterId}"
    const val MULTI_QR_GENERATE = "multi_qr_generate/{characterId}"
    const val MULTI_QR_SCAN     = "multi_qr_scan"
    const val MULTI_QR_PREVIEW  = "multi_qr_preview"

    fun characterDetail(characterId: Long) = "character_detail/$characterId"
    fun qrDisplay(id: Long, noNotes: Boolean = false) = "qr_display/$id/$noNotes"
    fun multiQrGenerate(characterId: Long) = "multi_qr_generate/$characterId"
}

@Composable
fun LogRPGNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CHARACTER_LIST
    ) {
        composable(Routes.CHARACTER_LIST) {
            CharacterListScreen(
                navController = navController,
                onNavigateToCharacter = { characterId ->
                    navController.navigate(Routes.characterDetail(characterId))
                }
            )
        }

        composable(
            route = Routes.CHARACTER_DETAIL,
            arguments = listOf(
                navArgument("characterId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getLong("characterId") ?: return@composable
            CharacterDetailScreen(
                navController = navController,
                characterId = characterId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.MULTI_QR_GENERATE,
            arguments = listOf(navArgument("characterId") { type = NavType.LongType })
        ) { backStack ->
            val characterId = backStack.arguments?.getLong("characterId") ?: return@composable
            val context = LocalContext.current
            val app = context.applicationContext as Application
            val viewModel: CharacterDetailViewModel = viewModel(
                factory = CharacterDetailViewModelFactory(characterId, app)
            )
            val character = viewModel.character.collectAsState().value ?: return@composable
            MultiQrScreen(
                character      = character,
                viewModel      = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MULTI_QR_SCAN) {
            MultiQrScannerScreen(
                onComplete = { stats, abilities, items, notes ->
                    // ✅ Query params au lieu de path segments — évite le crash avec JSON encodé
                    val s   = Uri.encode(stats)
                    val ab  = abilities?.let { Uri.encode(it) }
                    val it2 = items?.let    { Uri.encode(it) }
                    val n   = notes?.let    { Uri.encode(it) }

                    val route = buildString {
                        append("multi_qr_preview?stats=$s")
                        if (ab  != null) append("&abilities=$ab")
                        if (it2 != null) append("&items=$it2")
                        if (n   != null) append("&notes=$n")
                    }
                    navController.navigate(route) {
                        popUpTo(Routes.MULTI_QR_SCAN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "multi_qr_preview?stats={stats}&abilities={abilities}&items={items}&notes={notes}",
            arguments = listOf(
                navArgument("stats")     { type = NavType.StringType },
                navArgument("abilities") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("items")     { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("notes")     { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStack ->
            // ✅ Plus besoin de gérer "null" string manuellement, nullable = true le fait
            fun arg(key: String) = backStack.arguments?.getString(key)
                ?.let { Uri.decode(it) }
            val listViewModel: CharacterListViewModel = viewModel()

            MultiQrImportPreviewScreen(
                statsRaw     = arg("stats") ?: return@composable,
                abilitiesRaw = arg("abilities"),
                itemsRaw     = arg("items"),
                notesRaw     = arg("notes"),
                viewModel    = listViewModel,
                onImported   = { id ->
                    navController.navigate(Routes.characterDetail(id)) {
                        popUpTo(Routes.CHARACTER_LIST) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}