package cam.lucane.studio.log.rpg.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cam.lucane.studio.log.rpg.ui.screen.list.CharacterDetailScreen
import cam.lucane.studio.log.rpg.ui.screen.list.CharacterListScreen

object Routes {
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL = "character_detail/{characterId}"
    
    fun characterDetail(characterId: Long) = "character_detail/$characterId"
}

@Composable
fun LogRPGNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CHARACTER_LIST
    ) {
        composable(Routes.CHARACTER_LIST) {
            CharacterListScreen(
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
                characterId = characterId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
