package cam.lucane.studio.log.rpg.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import cam.lucane.studio.log.rpg.ui.screen.list.CharacterDetailScreen
import cam.lucane.studio.log.rpg.ui.screen.list.CharacterListScreen
import cam.lucane.studio.log.rpg.ui.screen.mj.MJScreen
import cam.lucane.studio.log.rpg.ui.screen.player.JoinSessionScreen
import cam.lucane.studio.log.rpg.ui.screen.player.JoinStep
import cam.lucane.studio.log.rpg.ui.viewmodel.*

object Routes {
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL = "character_detail/{characterId}"
    const val MJ_SCREEN    = "mj_screen?autoStart={autoStart}"
    const val JOIN_SESSION = "join_session"
    const val JOIN_SESSION_SWITCH = "join_session_switch"

    fun characterDetail(id: Long) = "character_detail/$id"
    fun mjScreen(autoStart: Boolean = false) = "mj_screen?autoStart=$autoStart"
}

@Composable
fun LogRPGNavigation(navController: NavHostController) {
    val mjViewModel:            MJViewModel            = viewModel()
    val playerSessionViewModel: PlayerSessionViewModel = viewModel()
    val characterListViewModel: CharacterListViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.CHARACTER_LIST) {

        composable(Routes.CHARACTER_LIST) {
            CharacterListScreen(
                viewModel              = characterListViewModel,
                playerSessionViewModel = playerSessionViewModel,
                onNavigateToCharacter  = { id -> navController.navigate(Routes.characterDetail(id)) },
                onCreateSession        = { navController.navigate(Routes.mjScreen(autoStart = true)) },
                onJoinSession          = { navController.navigate(Routes.JOIN_SESSION) },
                onSwitchCharacter      = { navController.navigate(Routes.JOIN_SESSION_SWITCH) }
            )
        }

        composable(
            route = Routes.CHARACTER_DETAIL,
            arguments = listOf(navArgument("characterId") { type = NavType.LongType })
        ) { back ->
            val characterId = back.arguments?.getLong("characterId") ?: return@composable
            CharacterDetailScreen(
                characterId            = characterId,
                playerSessionViewModel = playerSessionViewModel,
                onNavigateBack         = { navController.popBackStack() }
            )
        }

        // ✨ MJ Screen avec argument optionnel autoStart
        composable(
            route = Routes.MJ_SCREEN,
            arguments = listOf(
                navArgument("autoStart") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { back ->
            val autoStart = back.arguments?.getBoolean("autoStart") ?: false
            MJScreen(viewModel = mjViewModel, autoStart = autoStart, onNavigateBack = {navController.navigate(Routes.CHARACTER_LIST)})
        }

        composable(Routes.JOIN_SESSION) {
            val characters by characterListViewModel.characters.collectAsState()
            JoinSessionScreen(
                characters        = characters,
                playerName        = playerSessionViewModel.playerName.collectAsState().value,
                onNameChange      = playerSessionViewModel::setPlayerName,
                onQRScanned       = playerSessionViewModel::onQRScanned,
                onConnect         = playerSessionViewModel::connect,
                onCharacterPicked = { character ->
                    playerSessionViewModel.shareCharacter(character)
                    navController.popBackStack()
                },
                onCancel          = { navController.popBackStack() }
            )
        }

        composable(Routes.JOIN_SESSION_SWITCH) {
            val characters by characterListViewModel.characters.collectAsState()
            JoinSessionScreen(
                characters        = characters,
                playerName        = playerSessionViewModel.playerName.collectAsState().value,
                onNameChange      = playerSessionViewModel::setPlayerName,
                onQRScanned       = playerSessionViewModel::onQRScanned,
                onConnect         = playerSessionViewModel::connect,
                onCharacterPicked = { character ->
                    playerSessionViewModel.shareCharacter(character)
                    navController.popBackStack()
                },
                onCancel          = { navController.popBackStack() },
                startStep         = JoinStep.PICK
            )
        }
    }
}